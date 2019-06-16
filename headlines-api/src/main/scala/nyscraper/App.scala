package nyscraper

import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.functor._

object AppIo extends IOApp with Application {
  override def run(args: List[String]): IO[ExitCode] = program[IO].use(_ => IO.never.as(ExitCode.Success))
}
