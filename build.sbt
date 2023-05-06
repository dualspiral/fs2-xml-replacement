ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "fs2xml",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.4.10",
      "co.fs2" %% "fs2-core" % "3.6.1",
      "co.fs2" %% "fs2-io" % "3.6.1",
      "org.gnieh" %% "fs2-data-xml" % "1.7.0",
      "org.scalatest" %% "scalatest" % "3.2.15" % "test"
    )
  )
