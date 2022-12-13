package checkers.core

/**
  * @param snapshot - the state of the game at the beginning of the turn
  * @param play     - the play submitted by the player
  */
case class HistoryEntry(snapshot: Snapshot,
                        play: Play)
