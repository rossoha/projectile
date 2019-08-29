package com.kyleu.projectile.util.tracing

import com.kyleu.projectile.util.metrics.MetricsConfig
import io.opentracing.propagation._
import io.opentracing.{Span, Tracer}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

/** Implements TracingService to provide system-wide tracing support */
@javax.inject.Singleton
class OpenTracingService @javax.inject.Inject() (cnf: MetricsConfig)(implicit ec: ExecutionContext) extends TracingService {
  override def noopTrace[A](name: String)(f: TraceData => Future[A]) = {
    f(TraceData.noop)
  }
  override def topLevelTrace[A](name: String)(f: TraceData => Future[A]) = {
    val span = tracer.buildSpan(name).start()
    span.setTag("top.level", "true")

    val result = f(TraceDataOpenTracing(span))
    result.onComplete {
      case Failure(t) => serverSend(span, "failed" -> s"Finished with exception: ${t.getMessage}")
      case Success(_) => serverSend(span)
    }
    result
  }
  override def topLevelTraceBlocking[A](name: String)(f: TraceData => A): A = {
    val span = tracer.buildSpan(name).start()
    span.setTag("top.level", "true")
    try {
      val result = f(TraceDataOpenTracing(span))
      serverSend(span)
      result
    } catch {
      case NonFatal(x) =>
        serverSend(span, "failed" -> s"Finished with exception: ${x.getMessage}")
        throw x
    }
  }

  val ct = new OpenTracingTracer(cnf)
  private[this] val tracer: Tracer = ct.tracer

  private[this] def newServerSpan(traceName: String, tags: (String, String)*)(implicit parentData: TraceData) = parentData match {
    case td: TraceDataOpenTracing =>
      val childSpan = tracer.buildSpan(traceName).asChildOf(td.span).start()
      tags.foreach { case (key, value) => childSpan.setTag(key, value) }
      Some(childSpan.setTag("thread.id", Thread.currentThread.getName))
    case _ => None
  }

  override def traceBlocking[A](traceName: String, tags: (String, String)*)(f: TraceData => A)(implicit parentData: TraceData) = parentData match {
    case _: TraceDataOpenTracing => newServerSpan(traceName, tags: _*).map { childSpan =>
      Try(f(TraceDataOpenTracing(childSpan))) match {
        case Success(result) =>
          childSpan.finish()
          result
        case Failure(t) =>
          childSpan.setTag("error.type", t.getClass.getSimpleName.stripSuffix("$"))
          childSpan.setTag("error.message", t.getMessage)
          childSpan.finish()
          throw t
      }
    }.getOrElse(f(parentData))
    case _ => f(parentData)
  }

  override def trace[A](traceName: String, tags: (String, String)*)(f: TraceData => Future[A])(implicit parentData: TraceData) = {
    val childSpan = newServerSpan(traceName, tags: _*)
    val result = f(childSpan.map(TraceDataOpenTracing.apply).getOrElse(parentData))
    result.onComplete[Unit] {
      case Failure(t) =>
        childSpan.foreach(_.setTag("error.type", t.getClass.getSimpleName.stripSuffix("$")))
        childSpan.foreach(_.setTag("error.message", t.getMessage))
      case _ => childSpan.foreach(_.finish())
    }
    result
  }

  def serverReceived(spanName: String, span: Span) = tracer.buildSpan(spanName).asChildOf(span).start()
  def serverSend(span: Span, tags: (String, String)*) = {
    tags.foreach { case (key, value) => span.setTag(key, value) }
    span.finish()
    span
  }

  def newSpan[A](name: String, headers: Map[String, String]) = {
    Option(tracer.extract(Format.Builtin.HTTP_HEADERS.asInstanceOf[Format[TextMapExtract]], new TextMapExtractAdapter(headers.asJava))) match {
      case Some(extracted) => tracer.buildSpan(name).asChildOf(extracted)
      case None => tracer.buildSpan(name)
    }
  }

  override def close() = ct.close()
}
