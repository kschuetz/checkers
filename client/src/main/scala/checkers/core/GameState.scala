package checkers.core

import checkers.consts._
import checkers.core.BeginTurnEvaluation.CanMove

case class GameState(rulesSettings: RulesSettings,
                     playerConfig: PlayerConfig,
                     board: BoardState,
                     turnToMove: Color,
                     turnIndex: Int,
                     darkState: Opaque,
                     lightState: Opaque,
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

  def acceptDraw: GameState = {
    val entry = HistoryEntry(turnIndex, turnToMove, board, drawStatus, Play.AcceptDraw)
    copy(turnIndex = turnIndex + 1,
      turnToMove = OPPONENT(turnToMove),
      history = entry :: history)
  }

  def moveTree: MoveTree = beginTurnEvaluation match {
    case CanMove(tree) => tree
    case _ => MoveTree.empty
  }

  def withDarkState(newState: Opaque): GameState = copy(darkState = newState)

  def withLightState(newState: Opaque): GameState = copy(lightState = newState)

  def addToClock(color: Color, amount: Double): GameState = {
    if(amount <= 0) this
    else if(color == DARK) copy(darkClock = darkClock + amount)
    else copy(lightClock = lightClock + amount)
  }

  def opponent: Color = OPPONENT(turnToMove)

}

