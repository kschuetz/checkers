package checkers.core

import checkers.consts._
import checkers.core.BeginTurnEvaluation.CanMove

case class GameState[DS, LS](rulesSettings: RulesSettings,
                             playerConfig: PlayerConfig[DS, LS],
                             board: BoardState,
                             turnToMove: Color,
                             turnIndex: Int,
                             darkState: DS,
                             lightState: LS,
                             drawStatus: DrawStatus,
                             beginTurnEvaluation: BeginTurnEvaluation,
                             darkClock: Double,
                             lightClock: Double,
                             history: List[HistoryEntry]) {

  def currentPlayer: PlayerDescription =
    if (turnToMove == DARK) playerConfig.darkPlayer else playerConfig.lightPlayer

  def turnsUntilDraw: Option[Int] = drawStatus match {
    case DrawProposed(_, endTurnIndex) => Some(endTurnIndex - turnIndex)
    case _ => None
  }

  def wasDrawProposedBy(color: Color): Boolean = drawStatus match {
    case DrawProposed(c, _) if c == color => true
    case _ => false
  }

  def acceptDraw: GameState[DS, LS] = {
    val entry = HistoryEntry(turnIndex, turnToMove, board, drawStatus, Play.AcceptDraw)
    copy(turnIndex = turnIndex + 1,
      turnToMove = OPPONENT(turnToMove),
      history = entry :: history)
  }

  def moveTree: MoveTree = beginTurnEvaluation match {
    case CanMove(tree) => tree
    case _ => MoveTree.empty
  }

}
