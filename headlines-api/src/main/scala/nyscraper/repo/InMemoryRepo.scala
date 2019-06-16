package nyscraper.repo

import java.net.URL

import cats.Monad
import nyscraper.model.News

object InMemoryRepo {
  def apply[F[_]:Monad](implicit F: Monad[F]): Repo[F] = () =>
      F.pure(List(News("Bitcoin to the moon", new URL("https://bitcoin.org"))))
}
