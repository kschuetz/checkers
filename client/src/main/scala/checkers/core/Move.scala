package checkers.core

import scala.scalajs.js

sealed trait Move

class SimpleMove(val code: Int) extends AnyVal {
  def fromSquare: Int = code & 31
  def toSquare: Int = (code >> 5) & 31
}

class CompoundMove(path: js.Array[Int]) extends Move

class CompoundMoveBuilder(fromSquare: Int) {
  var path = new js.Array[Int]
  path.push(fromSquare)

  def add(square: Int): Unit =
    path.push(square)

  def result: js.Array[Int] = {
    val retval = path
    path = null
    retval
  }
}

class MoveList(val simpleMoves: js.Array[SimpleMove],
               val compoundMoves: js.Array[CompoundMove]) {
  def isEmpty = false
  def size = simpleMoves.length + compoundMoves.length
}

object EmptyMoveList extends MoveList(SimpleMove.empty, CompoundMove.empty) {
  override def isEmpty = true
}



class MoveListBuilder {
  private var empty = true
  private var simpleMoves: js.Array[SimpleMove] = SimpleMove.empty
  private var compoundMoves: js.Array[CompoundMove] = CompoundMove.empty

  def addSimpleMove(from: Int, to: Int): Unit = {
    if(simpleMoves.isEmpty) {
      simpleMoves = new js.Array[SimpleMove]
    }
    empty = false
    simpleMoves.push(SimpleMove(from, to))
  }

  def addCompoundMove(path: js.Array[Int]): Unit = {
    if(compoundMoves.isEmpty) {
      compoundMoves = new js.Array[CompoundMove]
    }
    empty = false
    compoundMoves.push(new CompoundMove(path))
  }

  def result: MoveList = {
    if(empty) EmptyMoveList
    else {
      val retval = new MoveList(simpleMoves, compoundMoves)
      simpleMoves = null
      compoundMoves = null
      retval
    }
  }
}

object SimpleMove {
  @inline
  def apply(from: Int, to: Int): SimpleMove =
    new SimpleMove((to << 5) | from)

  val empty = new js.Array[SimpleMove]

}

object CompoundMove {
  val empty = new js.Array[CompoundMove]
}