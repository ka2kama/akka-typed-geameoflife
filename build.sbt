name := "akka-typed-gameoflife"

version := "0.1"

organization := "com.ka2kama"

libraryDependencies ++= {
  val akkaVersion = "2.6.12"
  Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "ch.qos.logback"     % "logback-classic"  % "1.2.3",
  )
}
