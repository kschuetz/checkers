package checkers.core

import checkers.consts._
import checkers.core.BeginTurnEvaluation.CanMove

case class PlayerState(opaque: Opaque,
                       clock: Double)

case class GameState(rulesSettings: RulesSettings,
                     playerConfig: PlayerConfig,
                     board: BoardState,
                     turnToMove: Side,
                     turnIndex: Int,
                     darkState: PlayerState,
                     lightState: PlayerState,
                     drawStatus: DrawStatus,
                     beginTurnEvaluation: BeginTurnEvaluation,
                     history: List[HistoryEntry]) {

  def currentPlayer: PlayerDescription =
    if (turnToMove == DARK) playerConfig.darkPlayer else playerConfig.lightPlayer

  def turnsUntilDraw: Option[Int] = drawStatus match {
    case DrawProposed(_, endTurnIndex) => Some(endTurnIndex - turnIndex)
    case _ => None
  }

  def wasDrawProposedBy(side: Side): Boolean = drawStatus match {
    case DrawProposed(c, _) if c == side => true
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

  def playerState(side: Side): PlayerState = {
    if(side == DARK) darkState else lightState
  }

  def playerOpaque(side: Side): Opaque = playerState(side).opaque

  def withOpaque(side: Side, newOpaque: Opaque): GameState = {
    val prevState = playerState(side)
    val newState = prevState.copy(opaque = newOpaque)
    withPlayerState(side, newState)
  }

  def withPlayerState(side: Side, newState: PlayerState): GameState = {
    if(side == DARK) copy(darkState = newState) else copy(lightState = newState)
  }

  def clock(side: Side): Double = playerState(side).clock

  def addToClock(side: Side, amount: Double): GameState = {
    if(amount <= 0) this
    else {
      val prevState = playerState(side)
      val newState = prevState.copy(clock = prevState.clock + amount)
      withPlayerState(side, newState)
    }
  }

  def opponent: Side = OPPONENT(turnToMove)

}

