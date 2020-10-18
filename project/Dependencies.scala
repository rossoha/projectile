import sbt._

object Dependencies {
  object Play {
    private[this] val version = play.core.PlayVersion.current
    val filters = play.sbt.PlayImport.filters
    val cache = play.sbt.PlayImport.ehcache
    val guice = play.sbt.PlayImport.guice
    val mailer = "com.typesafe.play" %% "play-mailer-guice" % "8.0.1"
    val twirl = "com.typesafe.play" %% "twirl-api" % "1.5.0"
    val ws = play.sbt.PlayImport.ws

    val all = Seq(filters, guice, cache, twirl)
  }

  object Database {
    val postgres = "org.postgresql" % "postgresql" % "42.2.17"
    val hikariCp = "com.zaxxer" % "HikariCP" % "3.4.5"
    val flyway = "org.flywaydb" % "flyway-core" % "5.2.4"

    object Slick {
      val version = "3.3.2"
      val pgVersion = "0.19.0"

      val core = "com.typesafe.slick" %% "slick" % version
      val hikariCp = "com.typesafe.slick" %% "slick-hikaricp" % version
      val pg = "com.github.tminglei" %% "slick-pg" % pgVersion
      val pgCirce = "com.github.tminglei" %% "slick-pg_circe-json" % pgVersion
      val slickless = "io.underscore" %% "slickless" % "0.3.6"

      val all = Seq(core, hikariCp, pg, pgCirce, slickless)
    }

    object Doobie {
      val version = "0.8.7"

      val core = "org.tpolecat" %% "doobie-core" % version
      val hikariCp = "org.tpolecat" %% "doobie-hikari" % version
      val postgres = "org.tpolecat" %% "doobie-postgres" % version
      val testing = "org.tpolecat" %% "doobie-scalatest" % version % "test"

      val all = Seq(core, hikariCp, postgres, testing)
    }
  }

  object GraphQL {
    val sangria = "org.sangria-graphql" %% "sangria" % "2.0.0"
    val circe = "org.sangria-graphql" %% "sangria-circe" % "1.3.0"
  }

  object Serialization {
    val version = "0.12.3"

    val enumeratumCirceVersion = "1.6.0"
    val booPickleVersion = "1.3.3"

    val projects = Seq("circe-core", "circe-generic", "circe-parser")
    val jackson = "io.circe" %% "circe-jackson29" % "0.12.1"
  }

  object Thrift {
    object TwitterBijection {
      val version = "0.9.6"
      val core = "com.twitter" %% "bijection-core" % version
      val util = "com.twitter" %% "bijection-util" % version
      val all = Seq(core, util)
    }

    object Finagle {
      val version = "19.12.0"

      val core = "com.twitter" %% "finagle-core" % version
      val thrift = "com.twitter" %% "finagle-thrift" % version
      val thriftMux = "com.twitter" %% "finagle-thriftmux" % version

      val all = Seq(core, thrift, thriftMux)
    }

    val all = TwitterBijection.all ++ Finagle.all
  }

  object Metrics {
    val version = "1.5.5"
    val micrometerCore = "io.micrometer" % "micrometer-core" % version
    val micrometerPrometheus = "io.micrometer" % "micrometer-registry-prometheus" % version
    val micrometerStatsd = "io.micrometer" % "micrometer-registry-statsd" % version
  }

  object Tracing {
    val version = "1.4.0"
    val jaegerCore = "io.jaegertracing" % "jaeger-core" % version
    val jaegerThrift = "io.jaegertracing" % "jaeger-thrift" % version
    val jaegerMetrics = "io.jaegertracing" % "jaeger-micrometer" % version
    val datadogTracing = "com.datadoghq" % "dd-trace-ot" % "0.39.0"
  }

  object Authentication {
    private[this] val version = "7.0.0"
    val silhouette = "com.mohiva" %% "play-silhouette" % version excludeAll ExclusionRule(organization = "com.atlassian.jwt")
    val hasher = "com.mohiva" %% "play-silhouette-password-bcrypt" % version excludeAll ExclusionRule(organization = "com.atlassian.jwt")
    val persistence = "com.mohiva" %% "play-silhouette-persistence" % version excludeAll ExclusionRule(organization = "com.atlassian.jwt")
    val crypto = "com.mohiva" %% "play-silhouette-crypto-jca" % version excludeAll ExclusionRule(organization = "com.atlassian.jwt")

    val all = Seq(silhouette, hasher, persistence, crypto)
  }

  object WebJars {
    val autocomplete = "org.webjars.bower" % "EasyAutocomplete" % "1.3.3" intransitive()
    val fontAwesome = "org.webjars" % "font-awesome" % "4.7.0" intransitive()
    val jquery = "org.webjars" % "jquery" % "2.2.4" intransitive()
    val materialIcons = "org.webjars" % "material-design-icons" % "3.0.1"
    val materialize = "org.webjars" % "materializecss" % "1.0.0" intransitive()
    val swaggerUi = "org.webjars" % "swagger-ui" % "3.24.3" intransitive()

    val all = Seq(autocomplete, jquery, materialIcons, materialize, swaggerUi)
  }

  object Logging {
    val logback = "ch.qos.logback" % "logback-core" % "1.2.3"
    val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.2.3"
    val slf4j = "org.slf4j" % "slf4j-api" % "1.7.29"

    val all = Seq(logback, logbackClassic, slf4j)
  }

  object Compiler {
    private[this] val version = "1.4.2"
    val silencerPlugin = compilerPlugin("com.github.ghik" %% "silencer-plugin" % version)
    val silencerLib = "com.github.ghik" %% "silencer-lib" % version % Provided

    val all = Seq(silencerPlugin, silencerLib)
  }

  object Utils {
    val logback = "ch.qos.logback" % "logback-core" % "1.2.3"
    val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.2.3"
    val slf4j = "org.slf4j" % "slf4j-api" % "1.7.30"

    def betterFiles(useLatest: Boolean) = "com.github.pathikrit" %% "better-files" % (if(useLatest) { "3.9.1" } else { "3.7.1" })
    val chimney = "io.scalaland" %% "chimney" % "0.5.2"
    val clist = "org.backuity.clist" %% "clist-core"   % "3.5.1"
    val clistMacros = "org.backuity.clist" %% "clist-macros" % "3.5.1" % "provided"
    val commonsCodec = "commons-codec" % "commons-codec" % "1.13"
    val commonsIo = "commons-io" % "commons-io" % "2.7"
    val commonsLang = "org.apache.commons" % "commons-lang3" % "3.10"
    val csv = "com.github.tototoshi" %% "scala-csv" % "1.3.6"
    val enumeratum = "com.beachape" %% "enumeratum-circe" % Serialization.enumeratumCirceVersion
    val guava = "com.google.guava" % "guava" % "29.0-jre"
    val javaxInject = "javax.inject" % "javax.inject" % "1"
    val scalaGuice = "net.codingwell" %% "scala-guice" % "4.2.6"
    val thriftParser = "com.facebook.swift" % "swift-idl-parser" % "0.23.1"
    val typesafeConfig = "com.typesafe" % "config" % "1.4.0"
  }

  object Testing {
    val version = "3.1.0"
    val scalaTest = "org.scalatest" %% "scalatest" % version % "test"
  }
}
