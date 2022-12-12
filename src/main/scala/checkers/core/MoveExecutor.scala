package checkers.core

import checkers.consts._
import checkers.core.tables.JumpTable


case class RemovedPiece(piece: Occupant, squareIndex: Int)

case class MoveInfo(piece: Occupant,
                    fromSquare: Int,
                    toSquare: Int,
                    removedPiece: Option[RemovedPiece],
                    crowned: Boolean) {
  def isJump: Boolean = removedPiece.nonEmpty
  def isNormalMove: Boolean = removedPiece.isEmpty

  def getMoveEvents: Int ={
    var result = 0
    if(removedPiece.nonEmpty) result = result | PIECECAPTURED
    if(crowned) result = result | PIECECROWNED | PIECEADVANCED
    if(PIECETYPE(piece) == MAN) result = result | PIECEADVANCED
    result
  }
}

class MoveExecutor(rulesSettings: RulesSettings,
                   jumpTable: JumpTable) {

  /**
    * All methods have the same outcome, and update the board in place, but are used in different scenarios:
    *
    * fastExecute:
    *   Fast, can only handle one simple move segment, returns a flag indicating crowning event.
    *   Used in move generator.
    * executeFromMoveDecoder:
    *   Fast, can handle compound moves, returns a flag indicating move was a jump.
    *   Used in search routines.
    * execute:
    *   Slowest, can only handle one simple move segment, returns meta-data describing the move.
    *   Used when committing a move for a turn (not used in inner loops).
    */


  /**
    * Updates the board in place.  Does not return metadata, other than a flag indicating a crowning event.
    * @return if true, move ended in a piece being crowned
    */
  def fastExecute(boardState: MutableBoardState, from: Int, to: Int): Boolean = {
    var crowned = false
    val piece = boardState.getOccupant(from)
    val over = jumpTable.getMiddle(from, to)
    if(over >= 0) boardState.setOccupant(over, EMPTY)

    boardState.setOccupant(from, EMPTY)

    val m = 1 << to
    if(piece == LIGHTMAN && (m & checkers.masks.CROWNLIGHT) != 0) {
      crowned = true
      boardState.setOccupant(to, LIGHTKING)
    } else if (piece == DARKMAN && (m & checkers.masks.CROWNDARK) != 0) {
      crowned = true
      boardState.setOccupant(to, DARKKING)
      crowned = true
    } else {
      boardState.setOccupant(to, piece)
    }

    crowned
  }

  /**
    * Runs the contents of a MoveDecoder (which can include compound moves).
    * @return combination of possible flags: PIECECAPTURED, PIECEADVANCED, PIECECROWNED
    */
  def executeFromMoveDecoder(boardState: MutableBoardState, decoder: MoveDecoder): Int = {
    val len = decoder.pathLength
    assert(len > 1, "path length < 2!")
    val data = decoder.data
    var from = data(0)
    var to: Byte = 0
    var i = 1
    var returnEvents: Int = 0
    while (i < len) {
      to = data(i)

      val piece = boardState.getOccupant(from)

      val over = jumpTable.getMiddle(from, to)
      if(over >= 0) {
        returnEvents |= PIECECAPTURED
        boardState.setOccupant(over, EMPTY)
      }

      boardState.setOccupant(from, EMPTY)

      val m = 1 << to
      if(piece == LIGHTMAN && (m & checkers.masks.CROWNLIGHT) != 0) {
        boardState.setOccupant(to, LIGHTKING)
        returnEvents |= PIECECROWNED
      } else if (piece == DARKMAN && (m & checkers.masks.CROWNDARK) != 0) {
        boardState.setOccupant(to, DARKKING)
        returnEvents |= PIECECROWNED
      } else {
        boardState.setOccupant(to, piece)
      }

      if(PIECETYPE(piece) == MAN) returnEvents |= PIECEADVANCED

      from = to
      i += 1
    }

    returnEvents
  }

  /**
    * Updates the board in place.  Returns metadata describing the move.
    * Not as efficient as fastExecute.
    */
  def execute(boardState: MutableBoardState, from: Int, to: Int): MoveInfo = {
    val piece = boardState.getOccupant(from)
    var removedPiece = Option.empty[RemovedPiece]
    val overSquare = jumpTable.getMiddle(from, to)
    if(overSquare >= 0) {
      val overPiece = boardState.getOccupant(overSquare)
      if(ISPIECE(overPiece)) {
        removedPiece = Some(RemovedPiece(overPiece, overSquare))
      }
    }

    val crowned = fastExecute(boardState, from, to)
    MoveInfo(piece, from, to, removedPiece, crowned)
  }

}