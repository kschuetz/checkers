package checkers.core

import checkers.consts.Color

sealed trait Phase

object Phase {

  case class BeginHumanTurn(color: Color) extends Phase

}