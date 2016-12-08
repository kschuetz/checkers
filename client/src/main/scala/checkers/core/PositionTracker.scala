package checkers.core

import PositionTracker._

class PositionTracker private (positions: Map[Position, Int]) {
  def addPosition(boardState: BoardStateRead): (Int, PositionTracker) = {
    val key = (boardState.kings, boardState.darkPieces, boardState.lightPieces)
    val prev = positions.getOrElse(key, 0)
    val seen = prev + 1
    val newPositions = positions + (key -> seen)
    val result = new PositionTracker(newPositions)
    (seen, result)
  }
}

object PositionTracker {
  type Position = (Int, Int, Int)

  val empty: PositionTracker = new PositionTracker(Map.empty)
}