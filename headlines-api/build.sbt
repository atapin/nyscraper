import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "atapin"
ThisBuild / organizationName := "atapin"

lazy val root = (project in file("."))
  .settings(
    name := "headlines-api",
    libraryDependencies ++= Seq(
      cats % Compile,
      catsEffect % Compile,
      circe % Compile,
      circeOptics % Compile,
      sangria % Compile,
      sangriaCirce % Compile,
      quill % Compile,
      postgres % Compile,

      http4s % Compile,
      http4sServer % Compile,
      http4sCirce % Compile,

      slf4j % Compile,
      log4cats % Compile,

      scalaTest % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-language:postfixOps",
      "-language:higherKinds",
      "-Ypartial-unification")
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
