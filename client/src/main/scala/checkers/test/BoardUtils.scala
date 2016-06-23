package checkers.test

import checkers.consts._
import checkers.core.{BoardState, BoardStateRead}

object BoardUtils {

  def boardStatesEqual(b1: BoardStateRead, b2: BoardStateRead): Boolean = {
    val frame1 = BoardState.createFrame
    b1.copyFrameTo(frame1, 0)

    val frame2 = BoardState.createFrame
    b2.copyFrameTo(frame2, 0)

    var i = 0
    var result = true
    while(result && i < BoardState.frameSize) {
      if(frame1(i) == frame2(i)) i += 1
      else result = false
    }
    result
  }

  def squareMaskToSet(mask: Int): Set[Int] = {
    var board = mask
    var result = Set.empty[Int]
    var i = 0
    while (i < 31) {
      if((board & 1) == 1) result += i
      board = board >> 1
      i += 1
    }
    result
  }

//  def isJumperOfColor(boardState: BoardStateRead, color: Color)(squareIndex: Int): Boolean = {
//    val piece = boardState.getOccupant(squareIndex)
//    if(! (ISPIECE(piece) && COLOR(piece) == color)) return false
//
//    val opponent = if(color == LIGHT) DARK else LIGHT
//
//    def checkJump(m: Int, j: Int): Boolean = {
//      m >= 0 && j >= 0 && {
//        val over = boardState.getOccupant(m)
//        boardState.getOccupant(j) == EMPTY && ISPIECE(over) && COLOR(over) == opponent
//      }
//    }
//
//
//    val neighborIndex = NeighborIndex.forColor(color)
//    import neighborIndex._
//
//    if(checkJump(forwardMoveW(squareIndex), forwardJumpW(squareIndex))) return true
//    if(checkJump(forwardMoveE(squareIndex), forwardJumpE(squareIndex))) return true
//    if(PIECETYPE(piece) == KING) {
//      if(checkJump(backMoveW(squareIndex), backJumpW(squareIndex))) return true
//      if(checkJump(backMoveE(squareIndex), backJumpE(squareIndex))) return true
//    }
//    false
//  }

  val allSquares = Set(0 to 31)

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

    if(occupants.length != 32) throw new Exception("Input must have 32 squares")

    occupants.zip(parseSquareIndex).foldLeft(BoardState.empty){ case (result, (occ, idx)) =>
      result.updated(idx, occ)
    }

  }


}