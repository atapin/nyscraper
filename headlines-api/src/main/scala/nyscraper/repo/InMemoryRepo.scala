package nyscraper.repo

import java.net.URL

import cats.Applicative
import nyscraper.model.Headline

class InMemoryRepo[F[_]](implicit F: Applicative[F]) extends Repo[F] {
  private val storage = List(
    Headline("Bitcoin to the moon", "https://bitcoin.org"),
    Headline("Bitcoin falls down", "https://bitcoin.org")
  )
  override def list(): F[List[Headline]] = F.pure(storage)
}
