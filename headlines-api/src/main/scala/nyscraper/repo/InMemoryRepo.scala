package nyscraper.repo

import scala.collection.mutable.MutableList
import cats.Applicative
import nyscraper.model.Headline

import scala.collection.mutable

class InMemoryRepo[F[_]](implicit F: Applicative[F]) extends Repo[F] {
  private val storage: mutable.MutableList[Headline] = mutable.MutableList(
    Headline("Bitcoin to the moon", "https://bitcoin.org"),
    Headline("Bitcoin falls down", "https://bitcoin.org")
  )
  override def list(): F[List[Headline]] = F.pure(storage.toList)

  override def insert(news: Headline): F[Headline] = {
    storage += news
    F.pure(news)
  }
}
