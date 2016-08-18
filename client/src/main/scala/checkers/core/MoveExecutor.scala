package checkers.core

import checkers.consts._
import checkers.core.tables.JumpTable


case class RemovedPiece(piece: Occupant, squareIndex: Int)

case class MoveInfo(piece: Occupant,
                    fromSquare: Int,
                    toSquare: Int,
                    removedPiece: Option[RemovedPiece],
                    crowned: Boolean)

class MoveExecutor(rulesSettings: RulesSettings,
                   jumpTable: JumpTable) {

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
    if(piece == LIGHTMAN && (m & masks.crownLight) != 0) {
      crowned = true
      boardState.setOccupant(to, LIGHTKING)
    } else if (piece == DARKMAN && (m & masks.crownDark) != 0) {
      crowned = true
      boardState.setOccupant(to, DARKKING)
      crowned = true
    } else {
      boardState.setOccupant(to, piece)
    }

    crowned
  }

  /**
    * Updates the board in place.  Returns metadata describing the move.
    * Not as efficient as fastExecute.
    * @return
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