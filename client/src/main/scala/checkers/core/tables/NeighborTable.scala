package checkers.core.tables

import checkers.consts._
import checkers.core.Board

import scala.scalajs.js

case class NeighborIndex(forwardMoveW: js.Array[Int],
                         forwardMoveE: js.Array[Int],
                         forwardJumpW: js.Array[Int],
                         forwardJumpE: js.Array[Int],
                         forwardTwo: js.Array[Int],
                         backMoveW: js.Array[Int],
                         backMoveE: js.Array[Int],
                         backJumpW: js.Array[Int],
                         backJumpE: js.Array[Int],
                         backTwo:   js.Array[Int],
                         twoW:  js.Array[Int],
                         twoE: js.Array[Int])

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

  val twoN = makeNeighborList(-2, 0)
  val twoE = makeNeighborList(0, 2)
  val twoS = makeNeighborList(2, 0)
  val twoW = makeNeighborList(0, -2)

  val Dark = NeighborIndex(forwardMoveW = moveNW,
    forwardMoveE = moveNE,
    forwardJumpW = jumpNW,
    forwardJumpE = jumpNE,
    forwardTwo = twoN,
    backMoveW = moveSW,
    backMoveE = moveSE,
    backJumpW = jumpSW,
    backJumpE = jumpSE,
    backTwo = twoS,
    twoW = twoW,
    twoE = twoE)

  val Light = NeighborIndex(forwardMoveW = moveSW,
    forwardMoveE = moveSE,
    forwardJumpW = jumpSW,
    forwardJumpE = jumpSE,
    forwardTwo = twoS,
    backMoveW = moveNW,
    backMoveE = moveNE,
    backJumpW = jumpNW,
    backJumpE = jumpNE,
    backTwo = twoN,
    twoW = twoW,
    twoE = twoE)


  def forSide(side: Side): NeighborIndex = {
    if (side == DARK) Dark
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

