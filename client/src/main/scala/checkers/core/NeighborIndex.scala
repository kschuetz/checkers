package checkers.core

import scala.scalajs.js

trait NeighborIndex {
  val forwardMoveW: js.Array[Int]
  val forwardMoveE: js.Array[Int]
  val forwardJumpW: js.Array[Int]
  val forwardJumpE: js.Array[Int]
  val backMoveW: js.Array[Int]
  val backMoveE: js.Array[Int]
  val backJumpW: js.Array[Int]
  val backJumpE: js.Array[Int]
}

object NeighborIndex {
  val moveNW = makeNeighborList(-1, -1)
  val moveNE = makeNeighborList(-1, 1)
  val moveSW = makeNeighborList(1, -1)
  val moveSE = makeNeighborList(1, 1)

  val jumpNW = makeNeighborList(-2, -2)
  val jumpNE = makeNeighborList(-2, 2)
  val jumpSW = makeNeighborList(2, -2)
  val jumpSE = makeNeighborList(2, 2)

  private def makeNeighborList(rowOffset: Int, colOffset: Int): js.Array[Int] = {
    val result = new js.Array[Int]
    Board.position.foreach { pos =>
      val idx = pos.offset(rowOffset, colOffset).toSquareIndex
      result.push(idx)
    }
    result
  }
}

object DarkNeighborIndex extends NeighborIndex {
  import NeighborIndex._
  val forwardMoveW = moveNW
  val forwardMoveE = moveNE
  val forwardJumpW = jumpNW
  val forwardJumpE = jumpNE
  val backMoveW = moveSW
  val backMoveE = moveSE
  val backJumpW = jumpSW
  val backJumpE = jumpSE
}

object LightNeighborIndex extends NeighborIndex {
  import NeighborIndex._
  val forwardMoveW = moveNW
  val forwardMoveE = moveNE
  val forwardJumpW = jumpNW
  val forwardJumpE = jumpNE
  val backMoveW = moveSW
  val backMoveE = moveSE
  val backJumpW = jumpSW
  val backJumpE = jumpSE
}