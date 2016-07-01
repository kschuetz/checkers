package checkers.core

import checkers.consts._
import checkers.geometry.Point

sealed trait Phase

object Phase {

  case class BeginHumanTurn(color: Color, moves: MoveTree) extends Phase

  case class PieceSelected(piece: Occupant,
                           square: Int,
                           grabOffset: Point,
                           moves: MoveTree,
                           cancel: Phase) extends Phase

  case class MoveSegmentSubmitted(moves: MoveTree) extends Phase

  case class MovePartiallyCompleted(piece: Occupant,
                                    square: Int,
                                    moves: MoveTree) extends Phase


}