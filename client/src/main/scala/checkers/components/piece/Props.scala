package checkers.components.piece

import checkers.game.Piece
import japgolly.scalajs.react._

case class PieceMouseEvent(reactEvent: ReactMouseEvent,
                           piece: Piece,
                           tag: Int)

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
                              callbacks: PieceCallbacks)