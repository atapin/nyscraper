package nyscraper.graphql

import cats.{Monad, MonadError}
import cats.implicits._
import cats.effect._
import cats.effect.implicits._
import io.circe._
import io.circe.optics.JsonPath.root
import nyscraper.model.Headline
import nyscraper.repo.Repo
import sangria.ast.Document
import sangria.execution._
import sangria.marshalling.circe._
import sangria.parser.{QueryParser, SyntaxError}
import sangria.schema.{Field, ListType, ObjectType, Schema, StringType, fields}
import sangria.validation.AstNodeViolation

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Sangria {

  def newsType[F[_]]: ObjectType[Repo[F], Headline] = ObjectType(
    name = "News",
    fields = fields(
      Field(
        name = "title",
        fieldType = StringType,
        description = Some("News title."),
        resolve = _.value.title
      ),

      Field(
        name = "link",
        fieldType = StringType,
        description = Some("News link."),
        resolve = _.value.link
      )
    )
  )

  def queryType[F[_]: Effect]: ObjectType[Repo[F], Unit] = ObjectType(
    name = "Subscription",
    fields = fields(
      Field(
        name = "news",
        fieldType = ListType(newsType[F]),
        description = Some("News query."),
        resolve = c => c.ctx.list().toIO.unsafeToFuture()
      )
    )
  )

  def schema[F[_]: Effect]: Schema[Repo[F], Unit] = Schema(queryType[F])

  private def execute[F[_]](
    schema: Schema[Repo[F], Unit],
    query: Document,
    operation: Option[String],
    ctx: Repo[F],
    ec: ExecutionContext
  )(
    implicit F: MonadError[F, Throwable],
    L: LiftIO[F]
  ): F[Either[Json, Json]] = IO.fromFuture {
    implicit val executor: ExecutionContext = ec
    IO { Executor.execute(schema, query, ctx, operationName = operation) }
  }.to[F].attempt.flatMap {
      case Right(json)               => F.pure(json.asRight)
      case Left(err: WithViolations) => F.pure(errorWithViolations(err).asLeft)
      case Left(err)                 => F.pure(throwable(err).asLeft)
  }

  private def throwable(e: Throwable): Json = Json.obj(
    "errors" -> Json.arr(Json.obj(
      "class"   -> Json.fromString(e.getClass.getName),
      "message" -> Json.fromString(e.getMessage))))

  private def errorWithViolations(e: WithViolations): Json = Json.obj(
    "errors" -> Json.fromValues(e.violations.map {
      case v: AstNodeViolation => Json.obj(
        "message"   -> Json.fromString(v.errorMessage),
        "locations" -> Json.fromValues(v.locations.map(loc => Json.obj(
          "line"   -> Json.fromInt(loc.line),
          "column" -> Json.fromInt(loc.column)))))
      case v => Json.obj(
        "message" -> Json.fromString(v.errorMessage))})
  )

  private def stringError(s: String): Json = Json.obj(
    "errors" -> Json.arr(Json.obj(
      "message" -> Json.fromString(s))))

  private def syntaxError(e: SyntaxError): Json = Json.obj(
    "errors" -> Json.arr(Json.obj(
      "message"   -> Json.fromString(e.getMessage),
      "locations" -> Json.arr(Json.obj(
        "line"   -> Json.fromInt(e.originalError.position.line),
        "column" -> Json.fromInt(e.originalError.position.column))))))

  private val queryStringLens = root.query.string
  private val operationLens = root.operationName.string

  def apply[F[_]: Effect](repo: Repo[F], ec: ExecutionContext)(
    implicit F: Monad[F]
  ): GraphQL[F] = (request: Json) => {
    queryStringLens.getOption(request) match {
      case Some(qs) => QueryParser.parse(qs) match {
        case Success(ast) => execute(schema[F], ast, operationLens.getOption(request), repo, ec)
        case Failure(e @ SyntaxError(_, _, pe)) => F.pure(syntaxError(e).asLeft)
        case Failure(e) => F.pure(throwable(e).asLeft)
      }
      case None => F.pure(stringError("No query was found.").asLeft)
    }
  }
}
