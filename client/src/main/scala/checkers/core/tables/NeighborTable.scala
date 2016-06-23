package checkers.core.tables

import checkers.consts._
import checkers.core.Board

import scala.scalajs.js

case class NeighborIndex(forwardMoveW: js.Array[Int],
                         forwardMoveE: js.Array[Int],
                         forwardJumpW: js.Array[Int],
                         forwardJumpE: js.Array[Int],
                         backMoveW: js.Array[Int],
                         backMoveE: js.Array[Int],
                         backJumpW: js.Array[Int],
                         backJumpE: js.Array[Int])

class NeighborTable {
  import NeighborTable._

  val moveNW = makeNeighborList(-1, -1)
  val moveNE = makeNeighborList(-1, 1)
  val moveSW = makeNeighborList(1, -1)
  val moveSE = makeNeighborList(1, 1)

  val jumpNW = makeNeighborList(-2, -2)
  val jumpNE = makeNeighborList(-2, 2)
  val jumpSW = makeNeighborList(2, -2)
  val jumpSE = makeNeighborList(2, 2)

  val Dark = NeighborIndex(forwardMoveW = moveNW,
    forwardMoveE = moveNE,
    forwardJumpW = jumpNW,
    forwardJumpE = jumpNE,
    backMoveW = moveSW,
    backMoveE = moveSE,
    backJumpW = jumpSW,
    backJumpE = jumpSE)

  val Light = NeighborIndex(forwardMoveW = moveSW,
    forwardMoveE = moveSE,
    forwardJumpW = jumpSW,
    forwardJumpE = jumpSE,
    backMoveW = moveNW,
    backMoveE = moveNE,
    backJumpW = jumpNW,
    backJumpE = jumpNE)


  def forColor(color: Color): NeighborIndex = {
    if (color == DARK) Dark
    else Light
  }
}

object NeighborTable {
  private[tables] def makeNeighborList(rowOffset: Int, colOffset: Int): js.Array[Int] = {
    val result = new js.Array[Int]
    Board.position.foreach { pos =>
      val idx = pos.offset(rowOffset, colOffset).toSquareIndex
      result.push(idx)
    }
    result
  }
}

