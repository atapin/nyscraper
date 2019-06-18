package example

import cats.effect.Effect
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupDocument

trait PageReader[F[_]] {
  def readPage(): F[JsoupDocument]
}

object UrlReader {
  def apply[F[_]](url: String)(implicit F: Effect[F]): PageReader[F] = () =>
    F.delay(JsoupBrowser.typed().get(url))

}

object HtmlReader {
  def apply[F[_]](html: String)(implicit F: Effect[F]): PageReader[F] = () =>
    F.delay(JsoupBrowser.typed().parseString(html))
}