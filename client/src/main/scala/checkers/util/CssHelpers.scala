package checkers.util

import checkers.consts._

object CssHelpers {
  def playerColorClass(color: Color): String =
    if(color == DARK) "dark" else "light"
}