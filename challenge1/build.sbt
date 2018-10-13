name := "challenge1"

version := "0.1"

scalaVersion := "2.12.4"

lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion = "2.5.11"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion
)
