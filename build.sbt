val akkaVersion = "2.6.19"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "akka-typed-gameoflife",
    organization := "com.myorg",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.8",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "ch.qos.logback"     % "logback-classic"  % "1.2.11",
    ),
    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      // "-Xfatal-warnings",
      // "-Wconf:cat=lint-byname-implicit:s,any:e",
      "-Wconf:cat=lint-byname-implicit:s",
    ),
  )
