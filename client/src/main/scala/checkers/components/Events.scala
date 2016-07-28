package checkers.components

import checkers.consts._
import checkers.geometry.Point
import japgolly.scalajs.react._

case class PieceMouseEvent(reactEvent: ReactMouseEvent,
                           piece: Occupant,
                           tag: Int,
                           boardPoint: Point)

trait PieceCallbacks {
  def onPieceMouseDown: PieceMouseEvent => Option[Callback]
}

object EmptyPieceCallbacks extends PieceCallbacks {
  override def onPieceMouseDown: (PieceMouseEvent) => Option[Callback] = _ => {
    println("empty callback!")
    None
  }
}


case class SquareMouseEvent(reactEvent: ReactMouseEvent,
                            squareIndex: Int,
                            boardPoint: Point)


// Events that might occur on the board, but are not in game squares
case class BoardMouseEvent(reactEvent: ReactMouseEvent,
                           boardPoint: Point)


trait BoardCallbacks {
  def onSquareMouseDown: SquareMouseEvent => Option[Callback]
  def onBoardMouseDown: BoardMouseEvent => Option[Callback]
}

//object EmptyBoardCallbacks extends BoardCallbacks