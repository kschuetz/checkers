package checkers.core.old

import checkers.core.{Board, NeighborIndex}

import scala.scalajs.js

sealed trait Move {
  def isCompound: Boolean
  def isJump: Boolean
}



case class SimpleMove(fromSquare: Int, toSquare: Int, isJump: Boolean) extends Move {
  def isCompound = false
}

case class CompoundMove(reversePath: List[Int]) extends Move {
  def isCompound = true
  def isJump = true
}


object SimpleMoveIndex {
  private def encode(toSquare: Int, fromSquare: Int): Int =
    (toSquare << 5) | fromSquare

  private val index = {
    var result = Map.empty[Int, SimpleMove]

    def addMove(fromSquare: Int, toSquare: Int, isJump: Boolean): Unit =
      if(toSquare >= 0) {
        val move = SimpleMove(fromSquare, toSquare, isJump)
        result += (encode(fromSquare, toSquare) -> move)
      }

    Board.allSquares.foreach { i =>
      import NeighborIndex._
      addMove(i, moveNW(i), isJump=false)
      addMove(i, moveNE(i), isJump=false)
      addMove(i, moveSE(i), isJump=false)
      addMove(i, moveSW(i), isJump=false)
      addMove(i, jumpNW(i), isJump=true)
      addMove(i, jumpNE(i), isJump=true)
      addMove(i, jumpSE(i), isJump=true)
      addMove(i, jumpSW(i), isJump=true)
    }

    result
  }

  def apply(fromSquare: Int, toSquare: Int): SimpleMove = {
    val code = encode(fromSquare, toSquare)
    index.getOrElse(code, throw new Exception("Invalid SimpleMove"))
  }
}


class MoveList(val moves: js.Array[Move]) {
  def isEmpty = false
  def size = moves.length
}


object EmptyMoveList extends MoveList(new js.Array[Move]) {
  override def isEmpty = true
  override def size = 0
}


class MoveListBuilder {
  private var empty = true
  private var moves: js.Array[Move] = null

  def addSimpleMove(from: Int, to: Int): Unit = {
    if(moves.isEmpty) {
      moves = new js.Array[Move]
    }
    empty = false
    moves.push(SimpleMoveIndex(from, to))
  }

  def addCompoundMove(reversePath: List[Int]): Unit = {
    if(moves.isEmpty) {
      moves = new js.Array[Move]
    }
    empty = false
    moves.push(new CompoundMove(reversePath))
  }

  def result: MoveList = {
    if(empty) EmptyMoveList
    else {
      val retval = new MoveList(moves)
      moves = null
      retval
    }
  }
}

