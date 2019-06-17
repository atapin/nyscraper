package nyscraper.repo

import cats.Applicative
import io.getquill.{PostgresJdbcContext, SnakeCase}
import nyscraper.model.Headline

class PgRepo[F[_]](
  db: PostgresJdbcContext[SnakeCase]
)(
  implicit F: Applicative[F]
) extends Repo[F] {
  import db._

  override def list(): F[List[Headline]] = F.pure(
    db.run(quote {
      query[Headline]
    })
  )

  override def insert(news: Headline): F[Headline] = {
    db.run(quote {
      query[Headline].insert(lift(news)).onConflictIgnore
    })
    F.pure(news)
  }
}

