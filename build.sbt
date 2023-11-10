val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "TradingGame",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
  )
  libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % Test