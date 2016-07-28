package checkers.components

import checkers.consts._
import checkers.geometry.Point
import japgolly.scalajs.react._

case class PieceMouseEvent(reactEvent: ReactMouseEvent,
                           piece: Occupant,
                           tag: Int,
                           boardPoint: Point)

trait PieceCallbacks {
  def onPieceMouseDown: PieceMouseEvent => Option[Callback] = _ => None
}

object EmptyPieceCallbacks extends PieceCallbacks