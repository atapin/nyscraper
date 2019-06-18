package nyscraper.repo

import cats.Applicative
import cats.implicits._
import io.getquill.{PostgresJdbcContext, SnakeCase}
import nyscraper.model.Headline

class PgRepo[F[_]: Applicative](
  db: PostgresJdbcContext[SnakeCase]
) extends Repo[F] {
  import db._

  override def list(): F[List[Headline]] = db.run(quote {
    query[Headline]
  }).pure[F]

  override def insert(news: Headline): F[Headline] = {
    db.run(quote {
      query[Headline].insert(lift(news)).onConflictIgnore
    })
    news.pure[F]
  }
}


