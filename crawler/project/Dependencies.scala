import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val cats = "org.typelevel" %% "cats-core" % "2.0.0-M1"
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "1.3.1"
  lazy val scalaScraper = "net.ruippeixotog" %% "scala-scraper" % "2.1.0"
}
