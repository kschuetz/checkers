package checkers.components.piece

import checkers.game.Piece
import japgolly.scalajs.react._

case class PieceMouseEvent(reactEvent: ReactMouseEvent,
                           piece: Piece,
                           tag: Int)

trait PieceEvents {
  def onMouseDown: PieceMouseEvent => Option[Callback]
}

object EmptyPieceEvents extends PieceEvents {
  override val onMouseDown: (PieceMouseEvent) => Option[Callback] = _ => None
}


case class PhysicalPieceProps(piece: Piece,
                              tag: Int, // for events
                              x: Double,
                              y: Double,
                              scale: Double,
                              rotationDegrees: Double,
                              clickable: Boolean,
                              highlighted: Boolean,
                              events: PieceEvents)