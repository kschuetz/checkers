package checkers.core

import checkers.computer.PlayComputation
import checkers.consts._
import checkers.geometry.Point

sealed trait InputPhase

object InputPhase {

  case object GameStart extends InputPhase

  sealed trait HumanMovePhase extends InputPhase

  case object BeginHumanTurn extends HumanMovePhase

  case class PieceSelected(piece: Occupant,
                           square: Int,
                           nextMoveTree: MoveTree,
                           canCancel: Boolean) extends HumanMovePhase

  case class ComputerThinking[S](startTime: Double, playComputation: PlayComputation[S]) extends InputPhase

  case class GameOver(winner: Option[Color]) extends InputPhase


}