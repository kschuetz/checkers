package checkers.components.piece

import checkers.core.{DarkMan, Piece}
import checkers.geometry.Point
import japgolly.scalajs.react._

case class PieceMouseEvent(reactEvent: ReactMouseEvent,
                           piece: Piece,
                           tag: Int,
                           boardPoint: Point)

trait PieceCallbacks {
  def onPieceMouseDown: PieceMouseEvent => Option[Callback] = _ => None
}

object EmptyPieceCallbacks extends PieceCallbacks


case class PhysicalPieceProps(piece: Piece,
                              tag: Int, // for events
                              x: Double,
                              y: Double,
                              scale: Double,
                              rotationDegrees: Double,
                              clickable: Boolean,
                              highlighted: Boolean,
                              screenToBoard: Point => Point,
                              callbacks: PieceCallbacks)

object PhysicalPieceProps {
  val default = PhysicalPieceProps(piece = DarkMan,
    tag = 0,
    x = 0,
    y = 0,
    scale = 1,
    rotationDegrees = 0,
    clickable = false,
    highlighted = false,
    screenToBoard = identity,
    callbacks = EmptyPieceCallbacks)

}