package checkers.core

import checkers.consts.Color

sealed trait GameOverState

object GameOverState {
  case class Winner(color: Color, player: PlayerDescription) extends GameOverState
  case object Draw extends GameOverState
}