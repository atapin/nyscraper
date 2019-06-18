package nyscraper

import cats.Applicative
import cats.effect._
import cats.implicits._
import com.softwaremill.sttp._
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument

import scala.collection.mutable

case class Headline(title: String, link: String) {
  def mutation() = s"""addNews(title: \\"$title\\", link: \\"https://nytimes.com$link\\")"""
}

object CrawlerApp extends IOApp {

  private val api = "http://localhost:8080/graphql"

  override def run(args: List[String]): IO[ExitCode] = crawl[IO].as(ExitCode.Success)

  def query[F[_]: Applicative](news: List[Headline]): F[String] = {
    def mut(h: Headline, i: Int) = s"n$i: ${h.mutation()}"
    s"""mutation AddNews { ${news.mapWithIndex(mut).foldLeft("")((acc, it) => acc + s"  \\n$it")}}""".pure[F]
  }

  def parse[F[_]: Applicative](doc: JsoupDocument): F[List[Headline]] = doc.body
    .select("a > div > h2")
    .foldLeft(
      mutable.MutableList.empty[Headline]
    )(
      (l, el) => l += Headline(el.text, el.parent.get.parent.get.attr("href"))
    ).toList.pure[F]

  def crawl[F[_]: Effect]: F[Response[String]] = {
    val reader = UrlReader[F]("https://nytimes.com")
    for {
      page <- reader.readPage()
      news <- parse[F](page)
      q <- query[F](news)
      res <- sttpBackend[F].use(postNews[F](api, q)(_))
    } yield res
  }

  def sttpBackend[F[_] : Async]: Resource[F, SttpBackend[F, Nothing]] = {
    val alloc = Sync[F].delay(AsyncHttpClientCatsBackend[F]())
    val free = (bc: SttpBackend[F, Nothing]) => Sync[F].delay(bc.close())
    Resource.make(alloc)(free)
  }

  def postNews[F[_]](uri: String, query: String)(implicit sb: SttpBackend[F, Nothing]): F[Response[String]] =
    sttp
      .body(s"""{"operationName":"AddNews","variables":{},"query":"$query"}""")
      .post(uri"$uri")
      .send()



}