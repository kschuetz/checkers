package checkers.core

import checkers.consts._

/**
  * @param board - the state of the board *before* the play
  * @param drawStatus - the draw status *before* the play
  * @param play - the play submitted by the player
  */
case class HistoryEntry(turnIndex: Int,
                        turnToMove: Color,
                        board: BoardState,
                        drawStatus: DrawStatus,
                        play: Play)
