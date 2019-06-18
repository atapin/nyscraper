package nyscraper.repo

import cats.Applicative
import cats.implicits._
import nyscraper.model.Headline

import scala.collection.mutable

class InMemoryRepo[F[_]: Applicative] extends Repo[F] {
  private val storage: mutable.MutableList[Headline] = mutable.MutableList(
    Headline("Bitcoin to the moon", "https://bitcoin.org"),
    Headline("Bitcoin falls down", "https://bitcoin.org")
  )
  override def list(): F[List[Headline]] = storage.toList.pure[F]

  override def insert(news: Headline): F[Headline] = {
    storage += news
    news.pure[F]
  }
}
