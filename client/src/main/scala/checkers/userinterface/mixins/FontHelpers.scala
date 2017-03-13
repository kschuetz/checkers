package checkers.userinterface.mixins

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

trait FontHelpers {
  protected val fontSize: ReactStyle = VdomStyle("fontSize")
  protected val fontWeight: ReactStyle = VdomStyle("fontWeight")

  protected def textHeightPixels(value: Double): String =
    f"$value%.1fpx"
}