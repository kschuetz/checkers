package checkers.core

case class BoardPosition(row: Int, col: Int)


object Board {

  def isLegalSquareIndex(idx: Int): Boolean =
    idx >= 0 && idx < 32

  // squareIndex: 0..31
  def squareIndexToBoardPosition(squareIndex: Int): BoardPosition = {
    val row = squareIndex / 4
    val col = if (row % 2 == 0) 1 + 2 * (squareIndex % 4)
              else 2 * (squareIndex % 4)

    BoardPosition(row, col)
  }

  val allSquares = 0 to 31

  val position: Vector[BoardPosition] = allSquares.map(squareIndexToBoardPosition).toVector

  val lightStartingSquares = 0 to 11
  val darkStartingSquares = 20 to 31



}