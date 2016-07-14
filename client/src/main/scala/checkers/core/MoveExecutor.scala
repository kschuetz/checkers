package checkers.core

import checkers.consts._
import checkers.core.tables.JumpTable


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

  // TODO: apply Play
  // TODO: apply partial move
}