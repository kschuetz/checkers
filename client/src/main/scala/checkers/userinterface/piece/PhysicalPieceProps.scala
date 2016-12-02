package checkers.userinterface.piece

import checkers.userinterface.{BoardCallbacks, EmptyBoardCallbacks}
import checkers.consts._
import checkers.util.Point

case class PhysicalPieceProps(piece: Occupant,
                              tag: Int, // for events
                              x: Double,
                              y: Double,
                              scale: Double,
                              rotationDegrees: Double,
                              clickable: Boolean,
                              highlighted: Boolean,
                              ghost: Boolean,
                              screenToBoard: Point => Point,
                              callbacks: BoardCallbacks)

object PhysicalPieceProps {
  val default = PhysicalPieceProps(piece = DARKMAN,
    tag = 0,
    x = 0,
    y = 0,
    scale = 1,
    rotationDegrees = 0,
    clickable = false,
    highlighted = false,
    ghost = false,
    screenToBoard = identity,
    callbacks = EmptyBoardCallbacks)

}