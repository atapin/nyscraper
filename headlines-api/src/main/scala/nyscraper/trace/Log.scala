package nyscraper.trace

trait Log[F[_]] {
  def debug(message: String): F[Unit]
  def error(message: String): F[Unit]
}
