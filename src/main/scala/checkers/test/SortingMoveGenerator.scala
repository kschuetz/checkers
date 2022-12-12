package checkers.test

import checkers.consts._
import checkers.core._

// This decorates a MoveGenerator to sort its result before returning.
// It is very slow, so it is used only in tests to facilitate testing for symmetry.

class SortingMoveGenerator(source: MoveGenerator) extends MoveGenerator {
  def generateMoves(boardState: BoardStack, turnToMove: Side): MoveList = {
    val result = source.generateMoves(boardState, turnToMove)
    sorted(result, turnToMove)
  }

  def mustJump(boardState: BoardStateRead, turnToMove: Side): Boolean = source.mustJump(boardState, turnToMove)

  private def sorted(moveList: MoveList, turnToMove: Side): MoveList = {
    val paths = moveList.toList
    val sortedPaths = if(turnToMove == DARK) paths.sorted(PathOrderAscending) else paths.sorted(PathOrderDescending)
    val builder = new MoveListBuilder
    sortedPaths.foreach(builder.addPathFromList)
    builder.result
  }

}

private object PathOrderAscending extends Ordering[List[Int]] {
  override def compare(x: List[Int], y: List[Int]): Int = {
    var result = 0
    var p1 = x
    var p2 = y

    while(result == 0 && p1.nonEmpty && p2.nonEmpty) {
      val (i1 :: next1) = p1
      val (i2 :: next2) = p2
      if(i1 < i2) result = -1
      else if (i1 > i2) result = 1
      p1 = next1
      p2 = next2
    }
    result
  }
}

private object PathOrderDescending extends Ordering[List[Int]] {
  override def compare(x: List[Int], y: List[Int]): Int = PathOrderAscending.compare(y, x)
}