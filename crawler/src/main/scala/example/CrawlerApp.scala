package example

import java.net.URL

import cats.effect.{ExitCode, IO, IOApp}

case class Headline(title: String, href: URL)

object CrawlerApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = ???

  def crawl() = ???

}