package checkers.core

import checkers.consts._

sealed trait BeginTurnEvaluation

object BeginTurnEvaluation {
  case class CanMove(moveTree: MoveTree) extends BeginTurnEvaluation
  case object Draw extends BeginTurnEvaluation
  case class Win(color: Color) extends BeginTurnEvaluation
}

case class BeginTurnState(board: BoardState,
                          turnToMove: Color,
                          turnIndex: Int,
                          drawStatus: DrawStatus) {
  def turnsUntilDraw: Option[Int] = drawStatus match {
    case DrawProposed(_, endTurnIndex) => Some(endTurnIndex - turnIndex)
    case _ => None
  }
}

