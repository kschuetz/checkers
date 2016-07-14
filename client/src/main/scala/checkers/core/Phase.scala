package checkers.core

import checkers.computer.PlayComputation
import checkers.consts._
import checkers.geometry.Point

sealed trait Phase {
  def clickableSquares: Set[Int] = Set.empty[Int]
}

trait HumanMovePhase extends Phase {
  def moves: MoveTree
  override lazy val clickableSquares: Set[Int] =
    moves.squares
}


object Phase {

  case object GameStart extends Phase

  case class BeginHumanTurn(color: Color, moves: MoveTree) extends HumanMovePhase

  case class PieceSelected(piece: Occupant,
                           square: Int,
                           grabOffset: Point,
                           moves: MoveTree,
                           cancel: Phase) extends HumanMovePhase

  case class MoveSegmentSubmitted(moves: MoveTree) extends HumanMovePhase

  case class MovePartiallyCompleted(piece: Occupant,
                                    square: Int,
                                    moves: MoveTree) extends HumanMovePhase

  case class ComputerThinking[S](startTime: Double, playComputation: PlayComputation[S]) extends Phase

  case class GameOver(winner: Option[Color]) extends Phase


}