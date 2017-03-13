package checkers.userinterface.mixins

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.Attr

trait ClipPathHelpers {
  // for some reason, React insists on "clipPath" rather than "clip-path"
  protected val clipPathAttr: VdomAttr[Any] = Attr("clipPath")
}