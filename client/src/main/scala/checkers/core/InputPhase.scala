package checkers.core

import checkers.computer.PlayComputation
import checkers.consts._

sealed trait InputPhase {
  def waitingForHuman: Boolean = false
  def waitingForComputer: Boolean = false
  def waitingForAnimations: Boolean = false
  def endingTurn: Boolean = false
}

object InputPhase {

  case object GameStart extends InputPhase

  sealed trait HumanMovePhase extends InputPhase {
    override def waitingForHuman: Boolean = true
  }

  case object BeginHumanTurn extends HumanMovePhase

  case class PieceSelected(piece: Occupant,
                           square: Int,
                           validTargetSquares: Set[Int],
                           canCancel: Boolean) extends HumanMovePhase

  case class ComputerThinking(startTime: Double, playComputation: PlayComputation) extends InputPhase {
    override def waitingForComputer: Boolean = true
  }

  case class EndingTurn(nextTurnState: GameState) extends InputPhase {
    override def waitingForAnimations: Boolean = true
    override def endingTurn: Boolean = true
  }

  case class GameOver(winner: Option[Color]) extends InputPhase


}