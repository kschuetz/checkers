package checkers.components

import checkers.consts._
import checkers.geometry.Point
import japgolly.scalajs.react._

// squareIndex will be < 0 if not a playable square
case class BoardMouseEvent(reactEvent: ReactMouseEvent,
                           squareIndex: Int,
                           onPiece: Boolean,
                           piece: Occupant,
                           boardPoint: Point)

trait BoardCallbacks {
  def onBoardMouseDown: BoardMouseEvent => Option[Callback]
}

object EmptyBoardCallbacks extends BoardCallbacks {
  override def onBoardMouseDown: (BoardMouseEvent) => Option[Callback] = _ => {
    println("empty callback!")
    None
  }
}


// squareIndex will be < 0 if not a playable square
case class OldSquareMouseEvent(reactEvent: ReactMouseEvent,
                               squareIndex: Int,
                               boardPoint: Point)


trait OldBoardCallbacks {
  def onSquareMouseDown: OldSquareMouseEvent => Option[Callback]
}

//object EmptyBoardCallbacks extends BoardCallbacks