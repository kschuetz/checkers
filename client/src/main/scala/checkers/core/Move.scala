package checkers.core

import scala.scalajs.js


sealed trait Move

case class SimpleMove(from: Int, over: Int, to: Int) extends Move

/**
  * @param history - a list of simple moves (in reverse order).
  *                They are stored in reverse order
  */
case class CompoundMove(history: List[SimpleMove]) extends Move


object SimpleMoveIndex {
  private def encode(fromSquare: Int, toSquare: Int): Int =
    (toSquare << 5) | fromSquare

  val maxCode = encode(27, 31)

  val index = {

    val result = new js.Array[SimpleMove](maxCode)

    def addMove(fromSquare: Int, toSquare: Int): Unit =
      if(toSquare >= 0) {
        val move = SimpleMove(fromSquare, -1, toSquare)
        val code = encode(fromSquare, toSquare)
        result(code) = move
      }

    def addJump(fromSquare: Int, overSquare: Int, toSquare: Int): Unit =
      if(toSquare >= 0 && overSquare >= 0) {
        val move = SimpleMove(fromSquare, overSquare, toSquare)
        val code = encode(fromSquare, toSquare)
        result(code) = move
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
    * Returns null if move is invalid
    */
  def apply(fromSquare: Int, toSquare: Int): SimpleMove = {
    val code = encode(fromSquare, toSquare)
    index(code)
  }


}