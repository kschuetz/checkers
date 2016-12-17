package checkers.test

import checkers.consts._
import checkers.core.{BoardState, BoardStateRead}

object BoardUtils {

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

  val allSquares: Set[Int] = (0 to 31).toSet

  private val parseSquareIndex = List(
    28, 29, 30, 31, 24, 25, 26, 27, 20, 21, 22, 23, 16, 17, 18, 19,
    12, 13, 14, 15, 8, 9, 10, 11, 4, 5, 6, 7, 0, 1, 2, 3)

  def parseBoard(source: String): BoardState = {
    val occupants = source.collect {
      case '-' => EMPTY
      case 'd' => DARKMAN
      case 'l' => LIGHTMAN
      case 'D' => DARKKING
      case 'L' => LIGHTKING
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
    allSquares.foreach { idx =>
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