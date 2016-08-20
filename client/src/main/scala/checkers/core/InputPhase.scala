package checkers.core

import checkers.computer.PlayComputation
import checkers.consts._

sealed trait InputPhase {
  def waitingForHuman: Boolean = false
  def waitingForComputer: Boolean = false
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

  case class ComputerThinking[S](startTime: Double, playComputation: PlayComputation[S]) extends InputPhase {
    override def waitingForComputer: Boolean = true
  }

  case class GameOver(winner: Option[Color]) extends InputPhase


}