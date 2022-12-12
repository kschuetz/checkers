package checkers.core

import checkers.userinterface.board.PhysicalBoard
import checkers.consts._
import checkers.util.Point

import scala.collection.immutable.Range.Inclusive

//           28  29  30  31
//         24  25  26  27
//           20  21  22  23
//         16  17  18  19
//           12  13  14  15
//         08  09  10  11
//           04  05  06  07
//         00  01  02  03

case class BoardPosition(row: Int, col: Int) {
  def offset(rows: Int, cols: Int): BoardPosition =
    BoardPosition(row + rows, col + cols)
}


object Board {

  // returns -1 if illegal
  def boardPositionToSquareIndex(row: Int, col: Int): Int = {
    if(row < 0 || row > 7) return -1
    if(col < 0 || col > 7) return -1
    val r = row % 2
    val c = col % 2
    if (r == c) return -1

    (4 * (7 - row)) + (col - c) / 2
  }

  def boardPositionToSquareIndex(boardPosition: BoardPosition): Int =
    boardPositionToSquareIndex(boardPosition.row, boardPosition.col)

  def isLegalSquareIndex(idx: Int): Boolean =
    idx >= 0 && idx < 32

  // squareIndex: 0..31
  def squareIndexToBoardPosition(squareIndex: Int): BoardPosition = {
    val row = squareIndex / 4
    val col = if (row % 2 == 0) 2 * (squareIndex % 4)
              else 1 + 2 * (squareIndex % 4)

    BoardPosition(7 - row, col)
  }

  val playableSquares: Inclusive = 0 to 31

  val position: Vector[BoardPosition] = playableSquares.map(squareIndexToBoardPosition).toVector
  val squareCenter: Vector[Point] = position.map(PhysicalBoard.positionToPoint)

  lazy val allSquares: Seq[(BoardPosition, Int, Point)] = for {
    row <- 0 to 7
    col <- 0 to 7
  } yield {
    val boardPos = BoardPosition(row, col)
    (boardPos, boardPositionToSquareIndex(boardPos), PhysicalBoard.positionToPoint(boardPos))
  }

  val lightStartingSquares: Inclusive = 20 to 31
  val darkStartingSquares: Inclusive = 0 to 11

  val lightCrowningSquares: Set[Int] = (0 to 3).toSet
  val darkCrowningSquares: Set[Int] = (28 to 31).toSet

  def isNorthSquare(squareIndex: Int): Boolean = squareIndex >= 16

  def isSouthSquare(squareIndex: Int): Boolean = squareIndex < 16

  def isEastSquare(squareIndex: Int): Boolean = (squareIndex % 4) <= 1

  def isWestSquare(squareIndex: Int): Boolean = (squareIndex % 4) > 1

  def crowningSquares(side: Side): Set[Int] =
    if(side == DARK) darkCrowningSquares
    else lightCrowningSquares

  def isCrowningMove(piece: Occupant, destSquare: Int): Boolean = {
    if(piece == LIGHTMAN) lightCrowningSquares.contains(destSquare)
    else if(piece == DARKMAN) darkCrowningSquares.contains(destSquare)
    else false
  }

  def boardStatesEqual(b1: BoardStateRead, b2: BoardStateRead): Boolean = {
    b1.darkPieces == b2.darkPieces &&
      b1.lightPieces == b2.lightPieces && {
      val nonEmpty = b1.darkPieces & b1.lightPieces
      (b1.kings & nonEmpty) == (b2.kings & nonEmpty)
    }
  }

  def squareMaskToSet(mask: Int): Set[Int] = {
    var result = Set.empty[Int]
    var i = 0
    var j = 1
    while (i < 32) {
      if ((mask & j) != 0) result += i
      j <<= 1
      i += 1
    }
    result
  }

  private val parseSquareIndex = List(
    28, 29, 30, 31, 24, 25, 26, 27, 20, 21, 22, 23, 16, 17, 18, 19,
    12, 13, 14, 15, 8, 9, 10, 11, 4, 5, 6, 7, 0, 1, 2, 3)

  def parseBoard(source: String): BoardState = {
    val occupants = source.collect {
      case '-' => EMPTY
      case 'o' | 'd' => DARKMAN
      case 'x' | 'l' => LIGHTMAN
      case 'O' | 'D' => DARKKING
      case 'X' | 'L' => LIGHTKING
    }.toVector

    if (occupants.length != 32) throw new Exception("Input must have 32 squares")

    occupants.zip(parseSquareIndex).foldLeft(BoardState.empty) { case (result, (occ, idx)) =>
      result.updated(idx, occ)
    }
  }

  def mirror(input: BoardStateRead): BoardState = {
    var result = BoardState.empty
    var i = 0
    while (i < 32) {
      val occupant = input.getOccupant(i)
      val j = 31 - i

      val newOccupant =
        if (occupant == DARKMAN) LIGHTMAN
        else if (occupant == LIGHTMAN) DARKMAN
        else if (occupant == DARKKING) LIGHTKING
        else if (occupant == LIGHTKING) DARKKING
        else EMPTY

      result = result.updated(j, newOccupant)
      i += 1
    }
    result
  }

  case class BoardStats(empty: Int,
                        darkMan: Int,
                        darkKing: Int,
                        lightMan: Int,
                        lightKing: Int)

  def getBoardStats(board: BoardStateRead): BoardStats = {
    var empty = 0
    var darkMan = 0
    var darkKing = 0
    var lightMan = 0
    var lightKing = 0
    playableSquares.foreach { idx =>
      board.getOccupant(idx) match {
        case x if x == DARKMAN => darkMan += 1
        case x if x == DARKKING => darkKing += 1
        case x if x == LIGHTMAN => lightMan += 1
        case x if x == LIGHTKING => lightKing += 1
        case _ => empty += 1
      }
    }
    BoardStats(empty, darkMan, darkKing, lightMan, lightKing)
  }

}

