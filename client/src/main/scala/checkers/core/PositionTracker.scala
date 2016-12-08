package checkers.core

import PositionTracker._


trait PositionTracker {
  def addPosition(board: BoardStateRead): (Int, PositionTracker)
}

private object PositionTracker0 extends PositionTracker {
  def addPosition(board: BoardStateRead): (Int, PositionTracker) = (1, reset(board))
}

private class PositionTracker1(position1: Position) extends PositionTracker {
  def addPosition(board: BoardStateRead): (Int, PositionTracker) = {
    val position2 = getPosition(board)
    if(position2 != position1) {
      (1, new PositionTracker2(position1, position2))
    } else {
      (2, new MultiPositionTracker(Map(position1 -> 2)))
    }
  }
}

private class PositionTracker2(position1: Position, position2: Position) extends PositionTracker {
  def addPosition(board: BoardStateRead): (Int, PositionTracker) = {
    val position3 = getPosition(board)
    if (position3 == position1) {
      (2, new MultiPositionTracker(Map(position1 -> 2, position2 -> 1)))
    } else if (position3 == position2) {
      (2, new MultiPositionTracker(Map(position1 -> 1, position2 -> 2)))
    } else {
      (1, new MultiPositionTracker(Map(position1 -> 1, position2 -> 1, position3 -> 1)))
    }
  }
}


private class MultiPositionTracker(positions: Map[Position, Int]) extends PositionTracker {
  def addPosition(board: BoardStateRead): (Int, PositionTracker) = {
    val key = getPosition(board)
    val prev = positions.getOrElse(key, 0)
    val seen = prev + 1
    val newPositions = positions + (key -> seen)
    val result = new MultiPositionTracker(newPositions)
    (seen, result)
  }
}


object PositionTracker {
  type Position = (Int, Int, Int)

  val empty: PositionTracker = PositionTracker0

  def reset(board: BoardStateRead): PositionTracker =
    new PositionTracker1(getPosition(board))

  @inline
  def getPosition(board: BoardStateRead): Position =
    (board.kings, board.darkPieces, board.lightPieces)
}