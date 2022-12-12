package checkers.core

import checkers.consts.Side

sealed trait GameOverState

object GameOverState {
  case class Winner(side: Side, player: PlayerDescription) extends GameOverState
  case object Draw extends GameOverState
}