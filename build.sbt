ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"

lazy val root = (project in file("."))
  .settings(
    name := "introtoscalafx",
    libraryDependencies ++= {
      // Determine OS version of JavaFX binaries
      val osName = System.getProperty("os.name").toLowerCase match {
        case n if n.contains("linux")   => "linux"
        case n if n.contains("mac")     => "mac-aarch64" // Adjust for ARM-based Mac
        case n if n.contains("windows") => "win"
        case _                          => throw new Exception("Unknown platform!")
      }
      Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
        .map(m => "org.openjfx" % s"javafx-$m" % "21.0.4" classifier osName)
    },
    // Add ScalaFX dependency
    libraryDependencies ++= Seq("org.scalafx" %% "scalafx" % "21.0.0-R32"),

    // JavaFX runtime options for launching the application
    fork := true, // Ensure JavaFX runtime works by forking a JVM process
    javaOptions ++= Seq(
      "--module-path", "/Users/kevinyoong/Downloads/javafx-sdk-23.0.1/lib", // Update with your actual JavaFX SDK path
      "--add-modules", "javafx.controls,javafx.fxml"
    )
  )
