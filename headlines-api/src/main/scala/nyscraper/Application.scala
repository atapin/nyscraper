package nyscraper


import java.util.concurrent.{ExecutorService, Executors}

import cats.implicits._
import cats.effect._
import cats.effect.implicits._
import io.chrisdavenport.log4cats.Logger
import io.circe.Json
import io.getquill.{PostgresJdbcContext, SnakeCase}
import nyscraper.graphql.{GraphQL, Sangria}
import nyscraper.repo.{InMemoryRepo, PgRepo}
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.headers.Location
import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext

trait Application {

  def routes[F[_]: Sync: ContextShift](
    g: GraphQL[F], executionContext: ExecutionContext
  ): HttpRoutes[F] = {
    object routes extends Http4sDsl[F]
    import routes._

    HttpRoutes.of[F] {
      case req@POST -> Root / "graphql" => req.as[Json].flatMap(g.query).flatMap {
        case Right(json) => Ok(json)
        case Left(json) => BadRequest(json)
      }

      case GET -> Root / "playground.html" =>
        StaticFile
          .fromResource[F]("/playground.html", executionContext)
          .getOrElseF(NotFound())

      case _ =>
        PermanentRedirect(Location(uri"/playground.html"))
    }
  }

  def server[F[_]: ConcurrentEffect: ContextShift: Timer](
    routes: HttpRoutes[F]
  ): Resource[F, Server[F]] = BlazeServerBuilder[F]
    .bindHttp(8080, "localhost")
    .withHttpApp(routes.orNotFound)
    .resource

  def cachedThreadPool[F[_]](
    implicit F: Sync[F]
  ): Resource[F, ExecutionContext] = {
    val alloc = F.delay(Executors.newCachedThreadPool)
    val free  = (es: ExecutorService) => F.delay(es.shutdown())
    Resource.make(alloc)(free).map(ExecutionContext.fromExecutor)
  }

  def program[F[_]: ConcurrentEffect: ContextShift: Timer]: Resource[F, Server[F]] = for {
    ec <- cachedThreadPool[F]
    db = new PostgresJdbcContext[SnakeCase](SnakeCase, "ctx")
    repo = new PgRepo[F](db)
    g = Sangria[F](repo, ec)
    r = routes[F](g, ec)
    s <- server(r)
  } yield s
}
