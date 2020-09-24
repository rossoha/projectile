import sbt.Keys._
import sbt._

object Common {
  val projectId = "projectile"
  val projectName = "Projectile"
  val projectPort = 20000

  // After setting, toggle CollectionUtils import
  val useLatest = false

  object Versions {
    val app = "1.32.5.1"
    val scala212 = "2.12.12"
    val scala213 = "2.13.3"
  }

  private[this] val profilingEnabled = false
  private[this] val profileOptions = if (profilingEnabled) {
    "-Ystatistics:typer" +: Seq("no-profiledb", "show-profiles", "generate-macro-flamegraph").map(s => s"-P:scalac-profiling:$s")
  } else { Nil }

  val compileOptions = Seq(
    "-target:jvm-1.8", "-encoding", "UTF-8", "-feature", "-deprecation", "-explaintypes", "-feature", "-unchecked",
    "–Xcheck-null", "-Xlint", "-Xcheckinit", "-Yrangepos", "-Ywarn-dead-code", "-Ywarn-numeric-widen"
  ) ++ profileOptions

  lazy val settings = Seq(
    version := Common.Versions.app,
    scalaVersion := (if (useLatest) { Common.Versions.scala213 } else { Common.Versions.scala212 }),
    crossScalaVersions := Seq(Common.Versions.scala213, Common.Versions.scala212),
    organization := "com.kyleu",

    licenses := Seq("CC0" -> url("https://creativecommons.org/publicdomain/zero/1.0")),
    homepage := Some(url("https://projectile.kyleu.com")),
    scmInfo := Some(ScmInfo(url("https://github.com/KyleU/projectile"), "scm:git@github.com:KyleU/projectile.git")),
    developers := List(Developer(id = "kyleu", name = "Kyle Unverferth", email = "opensource@kyleu.com", url = url("http://kyleu.com"))),

    scalacOptions ++= {
      compileOptions ++ (if (scalaVersion.value.startsWith("2.13")) {
        Nil
      } else {
        Seq("-Xfatal-warnings", "-Xfuture", "-Ypartial-unification", "-Yno-adapted-args", "-Ywarn-inaccessible", "-Ywarn-nullary-override", "-Ywarn-infer-any")
      })
    },
    scalacOptions in (Compile, console) ~= (_.filterNot(Set("-Ywarn-unused:imports", "-Xfatal-warnings"))),
    scalacOptions in (Compile, doc) := Seq("-encoding", "UTF-8"),

    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnTransitiveEvictions(false),
    wartremover.WartRemover.autoImport.wartremoverWarnings ++= {
      import wartremover.Wart._
      Seq(
        ArrayEquals, EitherProjectionPartial, Enumeration, ExplicitImplicitTypes, FinalCaseClass,
        JavaConversions, JavaSerializable, LeakingSealed, OptionPartial,  Return, TraversableOps, TryPartial
      )
    },

    publishMavenStyle := true,
    // publishTo := Some(MavenRepository("sonatype-staging", "https://oss.sonatype.org/service/local/staging/deploy/maven2"))
    publishTo := xerial.sbt.Sonatype.autoImport.sonatypePublishToBundle.value
  ) ++ (if(profilingEnabled) { Seq(addCompilerPlugin("ch.epfl.scala" %% "scalac-profiling" % "1.0.0")) } else { Nil })

  def silencerOptions(path: String, pathFilters: Seq[String] = Nil, messageFilters: Seq[String] = Nil) = {
    Seq(s"-P:silencer:sourceRoots=$path") ++ (
      if(pathFilters.isEmpty) { Nil } else { Seq(s"-P:silencer:pathFilters=${pathFilters.mkString(";")}") }
    ) ++ (if(messageFilters.isEmpty) { Nil } else { Seq(s"-P:silencer:globalFilters=${messageFilters.mkString(";")}") })
  }
}
