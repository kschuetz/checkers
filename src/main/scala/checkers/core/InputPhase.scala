package checkers.core

import checkers.computer.PlayComputation
import checkers.consts._

sealed trait InputPhase {
  def waitingForHuman: Boolean = false
  def waitingForComputer: Boolean = false
  def waitingForMove: Boolean = false
  def waitingForAnimations: Boolean = false
  def endingTurn: Boolean = false
  def onTheClock: Boolean = true
}

object InputPhase {

  case class GameStart(nextState: GameState) extends InputPhase {
    override def waitingForAnimations: Boolean = true

    override def onTheClock: Boolean = false
  }

  sealed trait HumanMovePhase extends InputPhase {
    override def waitingForHuman: Boolean = true

    override def waitingForMove: Boolean = true
  }

  case object BeginHumanTurn extends HumanMovePhase

  case class PieceSelected(piece: Occupant,
                           square: Int,
                           validTargetSquares: Set[Int],
                           canCancel: Boolean) extends HumanMovePhase

  case class ComputerThinking(startTime: Double, playComputation: PlayComputation) extends InputPhase {
    override def waitingForComputer: Boolean = true

    override def waitingForMove: Boolean = true
  }

  case class EndingTurn(nextTurnState: GameState) extends InputPhase {
    override def waitingForAnimations: Boolean = true
    override def endingTurn: Boolean = true
  }

  case class GameOver(winner: Option[Side]) extends InputPhase {
    override def onTheClock: Boolean = false
  }


}