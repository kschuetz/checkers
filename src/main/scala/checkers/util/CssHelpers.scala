package checkers.util

import checkers.consts._

object CssHelpers {
  def playerSideClass(side: Side): String =
    if(side == DARK) "dark" else "light"

  def turnStatus(isPlayerTurn: Boolean): String =
    if(isPlayerTurn) "my-turn" else "not-my-turn"
}