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


class TurnEvaluator(moveGenerator: MoveGenerator,
                    moveTreeFactory: MoveTreeFactory) {
  import BeginTurnEvaluation._

  def evaluateBeginTurn[DS, LS](gameState: GameState[DS, LS]): BeginTurnEvaluation = {
    if(gameState.turnsUntilDraw.exists(_ <= 0)) Draw
    else {
      val turnToMove = gameState.turnToMove
      val boardStack = BoardStack.fromBoard(gameState.board)
      val moveList = moveGenerator.generateMoves(boardStack, gameState.turnToMove)
      val moveTree = moveTreeFactory.fromMoveList(moveList)
      if (moveTree.isEmpty) {
        if (gameState.config.rulesSettings.giveaway) Win(turnToMove)
        else Win(OPPONENT(turnToMove))
      } else {
        CanMove(moveTree)
      }
    }
  }

}