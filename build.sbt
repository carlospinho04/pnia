val Http4sVersion = "0.21.16"
val CirceVersion = "0.13.0"
val MunitVersion = "0.7.20"
val LogbackVersion = "1.2.3"
val MunitCatsEffectVersion = "0.13.0"
val BetterFilesVersion = "3.9.1"
//val pureConfigVersion = "0.14.0"
val pureConfig = "0.14.0"

enablePlugins(AshScriptPlugin)

lazy val root = (project in file("."))
  .settings(organization := "com.carlos",
            name := "pnia",
            version := "0.0.1-SNAPSHOT",
            scalaVersion := "2.13.4",
            libraryDependencies ++= Seq("org.http4s" %% "http4s-blaze-server" % Http4sVersion,
                                        "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
                                        "org.http4s" %% "http4s-circe" % Http4sVersion,
                                        "org.http4s" %% "http4s-dsl" % Http4sVersion,
                                        "io.circe" %% "circe-generic" % CirceVersion,
                                        "org.scalameta" %% "munit" % MunitVersion % Test,
                                        "org.typelevel" %% "munit-cats-effect-2" % MunitCatsEffectVersion % Test,
                                        "ch.qos.logback" % "logback-classic" % LogbackVersion,
                                        "com.github.pathikrit" %% "better-files" % BetterFilesVersion,
                                        "com.github.pureconfig" %% "pureconfig" % pureConfig,
                                        "com.github.pureconfig" %% "pureconfig-enumeratum" % pureConfig),
            addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
            addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
            testFrameworks += new TestFramework("munit.Framework"))
