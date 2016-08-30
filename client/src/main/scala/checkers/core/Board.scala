package checkers.core

import checkers.components.board.PhysicalBoard
import checkers.geometry.Point
import checkers.consts._

//           28  29  30  31
//         24  25  26  27
//           20  21  22  23
//         16  17  18  19
//           12  13  14  15
//         08  09  10  11
//           04  05  06  07
//         00  01  02  03

case class BoardPosition(row: Int, col: Int) {
  // returns -1 if illegal
  def toSquareIndex: Int = {
    if(row < 0 || row > 7) return -1
    if(col < 0 || col > 7) return -1
    val r = row % 2
    val c = col % 2
    if (r == c) return -1

    (4 * (7 - row)) + (col - c) / 2
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
    val col = if (row % 2 == 0) 2 * (squareIndex % 4)
              else 1 + 2 * (squareIndex % 4)

    BoardPosition(7 - row, col)
  }

  val playableSquares = 0 to 31

  val position: Vector[BoardPosition] = playableSquares.map(squareIndexToBoardPosition).toVector
  val squareCenter: Vector[Point] = position.map(PhysicalBoard.positionToPoint)

  lazy val allSquares: Seq[(BoardPosition, Int, Point)] = for {
    row <- 0 to 7
    col <- 0 to 7
  } yield {
    val boardPos = BoardPosition(row, col)
    (boardPos, boardPos.toSquareIndex, PhysicalBoard.positionToPoint(boardPos))
  }

  val lightStartingSquares = 20 to 31
  val darkStartingSquares = 0 to 11

  val lightCrowningSquares = (0 to 3).toSet
  val darkCrowningSquares = (28 to 31).toSet

  def isNorthSquare(squareIndex: Int): Boolean = squareIndex >= 16

  def isSouthSquare(squareIndex: Int): Boolean = squareIndex < 16

  def isEastSquare(squareIndex: Int): Boolean = (squareIndex % 4) <= 1

  def isWestSquare(squareIndex: Int): Boolean = (squareIndex % 4) > 1

  def crowningSquares(color: Color): Set[Int] =
    if(color == DARK) darkCrowningSquares
    else lightCrowningSquares

  def isCrowningMove(piece: Occupant, destSquare: Int): Boolean = {
    if(piece == LIGHTMAN) lightCrowningSquares.contains(destSquare)
    else if(piece == DARKMAN) darkCrowningSquares.contains(destSquare)
    else false
  }


}

