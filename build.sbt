val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "TradingGameScala",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % Test,
    libraryDependencies += "org.scalafx" %% "scalafx" % "16.0.0-R24",
    libraryDependencies += "com.oracle.database.jdbc" % "ojdbc8" % "19.8.0.0",
    libraryDependencies += "net.codingwell" %% "scala-guice" % "7.0.0",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.3",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.2.0",
    libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.3",
    libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.34.0",
    libraryDependencies += "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % Test,
    libraryDependencies ++= {
      // Determine OS version of JavaFX binaries afd
      lazy val osName = System.getProperty("os.name") match {
        case n if n.startsWith("Linux") => "linux"
        case n if n.startsWith("Mac") => "mac"
        case n if n.startsWith("Windows") => "win"
        case _ => throw new Exception("Unknown platform!")
      }
      Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
        .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
    },
    unmanagedResourceDirectories in Compile += baseDirectory.value / "src" / "main" / "scala",
  )