package checkers.core.tables

import checkers.consts._
import checkers.core.Board

import scala.scalajs.js
import scala.scalajs.js.Array

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

  val moveNW: js.Array[Int] = makeNeighborList(-1, -1)
  val moveNE: js.Array[Int] = makeNeighborList(-1, 1)
  val moveSW: js.Array[Int] = makeNeighborList(1, -1)
  val moveSE: js.Array[Int] = makeNeighborList(1, 1)

  val jumpNW: js.Array[Int] = makeNeighborList(-2, -2)
  val jumpNE: js.Array[Int] = makeNeighborList(-2, 2)
  val jumpSW: js.Array[Int] = makeNeighborList(2, -2)
  val jumpSE: js.Array[Int] = makeNeighborList(2, 2)

  val twoN: js.Array[Int] = makeNeighborList(-2, 0)
  val twoE: js.Array[Int] = makeNeighborList(0, 2)
  val twoS: js.Array[Int] = makeNeighborList(2, 0)
  val twoW: js.Array[Int] = makeNeighborList(0, -2)

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

