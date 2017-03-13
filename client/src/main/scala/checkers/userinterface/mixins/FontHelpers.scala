package checkers.userinterface.mixins

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{ svg_<^ => svg }

trait FontHelpers {
  protected val fontSize: VdomAttr[Any] = VdomStyle("fontSize")
  protected val fontWeight: VdomAttr[Any] = VdomStyle("fontWeight")

  protected def textHeightPixels(value: Double): String =
    f"$value%.1fpx"
}