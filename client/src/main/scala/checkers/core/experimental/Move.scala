package checkers.core.experimental

import checkers.core.{Board, NeighborIndex}
import checkers.util.{DebugUtils, NativeMap}


sealed trait Move

case class SimpleMove(from: Int, over: Int, to: Int) extends Move

case class CompoundMove(path: List[SimpleMove]) extends Move


object SimpleMoveIndex {
  private def encode(toSquare: Int, fromSquare: Int): Int =
    (toSquare << 5) | fromSquare

  val index = {
    val result = new NativeMap[Int, SimpleMove]

    def addMove(fromSquare: Int, toSquare: Int): Unit =
      if(toSquare >= 0) {
        val move = SimpleMove(fromSquare, -1, toSquare)
        result.set(encode(fromSquare, toSquare), move)
      }

    def addJump(fromSquare: Int, overSquare: Int, toSquare: Int): Unit =
      if(toSquare >= 0 && overSquare >= 0) {
        val move = SimpleMove(fromSquare, overSquare, toSquare)
        result.set(encode(fromSquare, toSquare), move)
      }

    Board.allSquares.foreach { i =>
      import NeighborIndex._
      addMove(i, moveNW(i))
      addMove(i, moveNE(i))
      addMove(i, moveSE(i))
      addMove(i, moveSW(i))
      addJump(i, moveNW(i), jumpNW(i))
      addJump(i, moveNE(i), jumpNE(i))
      addJump(i, moveSE(i), jumpSE(i))
      addJump(i, moveSW(i), jumpSW(i))
    }

    result
  }

  /**
    * Throws an exception if move is invalid
    */
  def apply(fromSquare: Int, toSquare: Int): SimpleMove = {
    val code = encode(fromSquare, toSquare)
    val result = index.get(code)
    result.get
  }


}