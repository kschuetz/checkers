package checkers.core

import checkers.computer.Program
import checkers.consts._
import checkers.core.BeginTurnEvaluation.CanMove

case class GameState(rulesSettings: RulesSettings,
                     playerConfig: PlayerConfig,
                     mentorConfig: MentorConfig,
                     gameClock: Double,
                     board: BoardState,
                     turnToMove: Side,
                     turnIndex: Int,
                     darkState: PlayerState,
                     lightState: PlayerState,
                     drawStatus: DrawStatus,
                     beginTurnEvaluation: BeginTurnEvaluation,
                     history: Vector[HistoryEntry]) {

  def currentPlayer: PlayerDescription =
    if (turnToMove == DARK) playerConfig.darkPlayer else playerConfig.lightPlayer

//  // TODO: rewrite this
//  def acceptDraw(snapshot: Snapshot): GameState = {
//    val entry = HistoryEntry(snapshot, Play.AcceptDraw)
//    copy(turnIndex = turnIndex + 1,
//      turnToMove = OPPONENT(turnToMove),
//      history = entry :: history)
//  }

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

  def withMentorOpaque(side: Side, newOpaque: Opaque): GameState = {
    val prevState = playerState(side)
    val newState = prevState.copy(mentorOpaque = Option(newOpaque))
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

  def mentor(side: Side): Option[(Program, Opaque)] = for {
    mentor <- mentorConfig.getMentor(side)
    mentorOpaque <- playerState(side).mentorOpaque
  } yield (mentor, mentorOpaque)

  def currentMentor: Option[(Program, Opaque)] = mentor(turnToMove)

  def hasMentor(side: Side): Boolean = mentorConfig.hasMentor(side) && playerState(side).mentorOpaque.isDefined

}

