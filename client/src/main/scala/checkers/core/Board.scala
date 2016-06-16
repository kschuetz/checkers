package checkers.core

import checkers.consts._

case class BoardPosition(row: Int, col: Int) {
  // returns -1 if illegal
  def toSquareIndex: Int = {
    if(row < 0 || row > 7) return -1
    if(col < 0 || col > 7) return -1
    val r = row % 2
    val c = col % 2
    if (r == c) return -1

    (4 * row) + (col - c) / 2
  }

  def offset(rows: Int, cols: Int): BoardPosition =
    BoardPosition(row + rows, col + cols)
}


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

  val lightCrowningSquares = (28 to 31).toSet
  val darkCrowningSquares = (0 to 3).toSet

  def crowningSquares(color: Color): Set[Int] =
    if(color == DARK) darkCrowningSquares
    else lightCrowningSquares

  def isCrowningMove(piece: Occupant, destSquare: Int): Boolean = {
    if(piece == LIGHTMAN) lightCrowningSquares.contains(destSquare)
    else if(piece == DARKMAN) darkCrowningSquares.contains(destSquare)
    else false
  }


}

