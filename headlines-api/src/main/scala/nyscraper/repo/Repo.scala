package nyscraper.repo

import nyscraper.model.News

trait Repo[F[_]] {
  def list(): F[List[News]]
}
