package checkers.userinterface.mixins

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

trait FontHelpers {
  protected val fontSize: ReactStyle = "fontSize".reactStyle
  protected val fontWeight: ReactStyle = "fontWeight".reactStyle
}