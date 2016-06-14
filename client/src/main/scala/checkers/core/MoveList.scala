package checkers.core

import scala.scalajs.js

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

  def addSimpleMove(move: SimpleMove): Unit = {
    if(moves.isEmpty) {
      moves = new js.Array[Move]
      empty = false
    }
    moves.push(move)
  }

  def addPath(path: List[SimpleMove]): Unit = {
    path match {
      case Nil => ()
      case one :: Nil => addSimpleMove(one)
      case many =>
        val compoundMove = CompoundMove(many)
        if(moves.isEmpty) {
          moves = new js.Array[Move]
          empty = false
        }
        moves.push(compoundMove)
    }
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
