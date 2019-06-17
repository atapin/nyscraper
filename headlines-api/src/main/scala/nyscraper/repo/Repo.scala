package nyscraper.repo

import nyscraper.model.Headline

trait Repo[F[_]] {
  def list(): F[List[Headline]]
  def insert(news: Headline): F[Headline]
}
