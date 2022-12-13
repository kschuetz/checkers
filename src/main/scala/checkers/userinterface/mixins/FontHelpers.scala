package checkers.userinterface.mixins

import japgolly.scalajs.react.vdom.html_<^._

trait FontHelpers {
  protected val fontSize: VdomAttr[Any] = VdomStyle("fontSize")
  protected val fontWeight: VdomAttr[Any] = VdomStyle("fontWeight")

  protected def textHeightPixels(value: Double): String =
    f"$value%.1fpx"
}
