package checkers.userinterface.mixins

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{Attr, SvgTagOf}
import org.scalajs.dom.svg.ClipPath

trait ClipPathHelpers {
  // for some reason, React insists on "clipPath" rather than "clip-path"
  protected val clipPathAttr: VdomAttr[Any] = Attr("clipPath")

  // scala-js-react workaround
  protected val clipPathTag: SvgTagOf[ClipPath] = SvgTagOf[org.scalajs.dom.svg.ClipPath]("clipPath")
}
