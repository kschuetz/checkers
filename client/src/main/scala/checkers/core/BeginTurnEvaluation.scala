package checkers.core

import checkers.consts._

sealed trait BeginTurnEvaluation {
  def requiresJump: Boolean = false
  def isGameOver: Boolean = false
}

object BeginTurnEvaluation {
  case class CanMove(moveTree: MoveTree) extends BeginTurnEvaluation {
    override def requiresJump: Boolean = moveTree.requiresJump
  }
  case object Draw extends BeginTurnEvaluation {
    override val isGameOver = true
  }
  case class Win(side: Side) extends BeginTurnEvaluation {
    override def isGameOver = true
  }
}

case class BeginTurnState(board: BoardState,
                          turnToMove: Side,
                          turnIndex: Int,
                          drawStatus: DrawStatus) {
  def turnsUntilDraw: Option[Int] = drawStatus.turnsRemainingHint
}

