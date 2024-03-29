
organization := "com.andreyatapin"
name := "nyscraper"
version := "0.0.1"
scalaVersion := "2.12.8"

val http4sVersion = "0.20.0"
val sangriaVersion = "1.4.2"

lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
lazy val cats = "org.typelevel" %% "cats-core" % "2.0.0-M1"
lazy val catsEffect = "org.typelevel" %% "cats-effect" % "1.3.1"
lazy val sangria = "org.sangria-graphql" %% "sangria" % sangriaVersion
lazy val sangriaCirce = "org.sangria-graphql" %% "sangria-circe" % "1.2.1"
lazy val quill = "io.getquill" %% "quill-jdbc" % "3.2.0"
lazy val postgres = "org.postgresql" % "postgresql" % "9.4.1208"
lazy val circe = "io.circe" % "circe-core_2.12" % "0.11.0"
lazy val circeOptics = "io.circe" %% "circe-optics" % "0.11.0"
lazy val http4s = "org.http4s" %% "http4s-dsl" % http4sVersion
lazy val http4sServer = "org.http4s" %% "http4s-blaze-server" % http4sVersion
lazy val http4sCirce = "org.http4s" %% "http4s-circe" % http4sVersion
lazy val log4cats = "io.chrisdavenport" %% "log4cats-slf4j" % "0.2.0"
lazy val slf4j = "org.slf4j" % "slf4j-simple" % "1.7.25"
lazy val scalaScraper = "net.ruippeixotog" %% "scala-scraper" % "2.1.0"
lazy val sttp = "com.softwaremill.sttp" %% "core" % "1.5.19"
lazy val sttpBack = "com.softwaremill.sttp" %% "async-http-client-backend-cats" % "1.5.19"


lazy val headlines_api = (project in file("headlines-api"))
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


lazy val crawler = (project in file("crawler"))
  .settings(
    name := "crawler",
    libraryDependencies ++= Seq(
      cats % Compile,
      catsEffect % Compile,
      scalaScraper % Compile,
      sttp % Compile,
      sttpBack % Compile,

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