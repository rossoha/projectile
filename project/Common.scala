import com.github.sbt.cpd.CpdKeys.cpdSkipDuplicateFiles
import sbt.Keys._
import sbt._
import sbtassembly.AssemblyPlugin.autoImport._

object Common {
  val projectId = "projectile"
  val projectName = "Projectile"
  val projectPort = 20000

  object Versions {
    val app = "1.1.0-SNAPSHOT"
    val scala = "2.12.8"
  }

  private[this] val profilingEnabled = false

  val compileOptions = Seq(
    "-target:jvm-1.8", "-encoding", "UTF-8", "-feature", "-deprecation", "-explaintypes", "-feature", "-unchecked",
    "–Xcheck-null", /* "-Xfatal-warnings", */ /* "-Xlint", */ "-Xcheckinit", "-Xfuture", "-Yrangepos", "-Ypartial-unification",
    "-Yno-adapted-args", "-Ywarn-dead-code", "-Ywarn-inaccessible", "-Ywarn-nullary-override", "-Ywarn-numeric-widen", "-Ywarn-infer-any"
  ) ++ (if (profilingEnabled) {
    "-Ystatistics:typer" +: Seq("no-profiledb", "show-profiles", "generate-macro-flamegraph").map(s => s"-P:scalac-profiling:$s")
  } else { Nil })

  lazy val settings = Seq(
    version := Common.Versions.app,
    scalaVersion := Common.Versions.scala,
    organization := "com.kyleu",

    licenses := Seq("CC0" -> url("https://creativecommons.org/publicdomain/zero/1.0")),
    homepage := Some(url("https://projectile.kyleu.com")),
    scmInfo := Some(ScmInfo(url("https://github.com/KyleU/projectile"), "scm:git@github.com:KyleU/projectile.git")),
    developers := List(Developer(id = "kyleu", name = "Kyle Unverferth", email = "opensource@kyleu.com", url = url("http://kyleu.com"))),

    scalacOptions ++= compileOptions,
    scalacOptions in (Compile, console) ~= (_.filterNot(Set("-Ywarn-unused:imports", "-Xfatal-warnings"))),
    scalacOptions in (Compile, doc) := Seq("-encoding", "UTF-8"),

    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnTransitiveEvictions(false).withWarnDirectEvictions(false),

    cpdSkipDuplicateFiles := true,

    publishMavenStyle := true,

    publishTo := xerial.sbt.Sonatype.SonatypeKeys.sonatypePublishTo.value
    // publishTo := Some("releases" at "http://nexus-1.fevo.com:8081/nexus/content/repositories/releases"),
  ) ++ (if(profilingEnabled) {
    Seq(addCompilerPlugin("ch.epfl.scala" %% "scalac-profiling" % "1.0.0"))
  } else {
    Nil
  })
}