package checkers.userinterface

import checkers.consts._
import checkers.util.Point
import japgolly.scalajs.react._

// squareIndex will be < 0 if not a playable square
case class BoardMouseEvent(reactEvent: ReactMouseEvent,
                           squareIndex: Int,
                           onPiece: Boolean,
                           piece: Occupant,
                           boardPoint: Point)

trait BoardCallbacks {
  def onBoardMouseDown: BoardMouseEvent => Option[Callback]
  def onBoardMouseMove: BoardMouseEvent => Option[Callback]
}

object EmptyBoardCallbacks extends BoardCallbacks {
  override val onBoardMouseDown: (BoardMouseEvent) => Option[Callback] = _ => {
    None
  }

  override val onBoardMouseMove: (BoardMouseEvent) => Option[Callback] = onBoardMouseDown
}

