name := "iguazu"

val IGUAZU_VERSION = "0.0.1"

val MAIN_SCALA = "2.13.4"
val ALL_SCALA  = Seq(MAIN_SCALA)

val ACONCAGUA_GRAPHQL_VERSION = "0.3.0"

val PRICE_VERSION = "0.3.0"

val STTP_VERSION = "3.2.0"

val ZIO_MAGIC_VERSION = "0.2.3"

val PARANA_VERSION = "0.4.1"

val ZIO_CONFIG_VERSION = "1.0.2"

val ZIO_JSON_VERSION = "0.1.3"

val ZIO_VERSION = "1.0.5"

inThisBuild(
  List(
    organization := "dev.palanga",
    homepage := Some(url("https://github.com/palanga/iguazu")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    parallelExecution in Test := false,
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/palanga/iguazu/"),
        "scm:git:git@github.com:palanga/iguazu.git",
      )
    ),
    developers := List(
      Developer(
        "palanga",
        "Andrés González",
        "a.gonzalez.terres@gmail.com",
        url("https://github.com/palanga"),
      )
    ),
    publishTo := Some("Artifactory Realm" at "https://palanga.jfrog.io/artifactory/maven/"),
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  )
)

lazy val root =
  (project in file("."))
    .settings(skip in publish := true)
    .aggregate(
      core,
      event_sourcing,
      api,
    )

lazy val core =
  (project in file("core"))
    .settings(commonSettings)
    .settings(
      name := "iguazu-core",
      version := IGUAZU_VERSION,
      fork in Test := true,
      fork in run := true,
      testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
      libraryDependencies ++= Seq(
        "dev.palanga"          %% "price"               % PRICE_VERSION,
        "io.github.kitlangton" %% "zio-magic"           % ZIO_MAGIC_VERSION,
        "dev.zio"              %% "zio-config"          % ZIO_CONFIG_VERSION,
        "dev.zio"              %% "zio-config-magnolia" % ZIO_CONFIG_VERSION,
        "dev.zio"              %% "zio-config-yaml"     % ZIO_CONFIG_VERSION,
        "dev.zio"              %% "zio-json"            % ZIO_JSON_VERSION,
        "dev.zio"              %% "zio-test"            % ZIO_VERSION % "test",
        "dev.zio"              %% "zio-test-sbt"        % ZIO_VERSION % "test",
      ),
    )

lazy val api =
  (project in file("api/graphql"))
    .settings(commonSettings)
    .settings(
      name := "iguazu-api-graphql",
      fork in Test := true,
      fork in run := true,
      testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
      libraryDependencies ++= Seq(
        "dev.palanga"   %% "aconcagua-graphql" % ACONCAGUA_GRAPHQL_VERSION,
        "ch.qos.logback" % "logback-classic"   % "1.2.3",
      ),
    )
    .dependsOn(
      event_sourcing,
      notifications,
    )

lazy val event_sourcing =
  (project in file("eventsourcing"))
    .settings(commonSettings)
    .settings(
      name := "iguazu-eventsourcing",
      fork in Test := true,
      fork in run := true,
      testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
      libraryDependencies ++= Seq(
        "dev.palanga" %% "parana-core"              % PARANA_VERSION,
        "dev.palanga" %% "parana-journal-cassandra" % PARANA_VERSION,
      ),
    )
    .dependsOn(core)

lazy val notifications =
  (project in file("notifications"))
    .settings(commonSettings)
    .settings(
      name := "iguazu-notifications",
      fork in Test := true,
      fork in run := true,
      testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
      libraryDependencies ++= Seq(
        "com.softwaremill.sttp.client3" %% "core"                   % STTP_VERSION,
        "com.softwaremill.sttp.client3" %% "httpclient-backend-zio" % STTP_VERSION,
      ),
    )
    .dependsOn(
      core,
      event_sourcing,
    )

val commonSettings =
  Def.settings(
    scalaVersion := MAIN_SCALA,
    crossScalaVersions := ALL_SCALA,
    libraryDependencies += compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    resolvers += "Artifactory" at "https://palanga.jfrog.io/artifactory/maven/",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-explaintypes",
      "-Yrangepos",
      "-feature",
      "-language:higherKinds",
      "-language:existentials",
      "-unchecked",
      "-Xlint:_,-type-parameter-shadow",
      //    "-Xfatal-warnings",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused:patvars,-implicits",
      "-Ywarn-value-discard",
      //      "-Ymacro-annotations",
    ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) =>
        Seq(
          "-Xsource:2.13",
          "-Yno-adapted-args",
          "-Ypartial-unification",
          "-Ywarn-extra-implicit",
          "-Ywarn-inaccessible",
          "-Ywarn-infer-any",
          "-Ywarn-nullary-override",
          "-Ywarn-nullary-unit",
          "-opt-inline-from:<source>",
          "-opt-warnings",
          "-opt:l:inline",
        )
      case _             => Nil
    }),
    //    scalacOptions in Test --= Seq("-Xfatal-warnings"),
  )
