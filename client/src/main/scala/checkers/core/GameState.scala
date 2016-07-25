package checkers.core

import checkers.consts._

case class GameState[DS, LS](rulesSettings: RulesSettings,
                             playerConfig: PlayerConfig[DS, LS],
                             board: BoardState,
                             turnToMove: Color,
                             turnIndex: Int,
                             darkState: DS,
                             lightState: LS,
                             drawStatus: DrawStatus,
                             beginTurnEvaluation: BeginTurnEvaluation,
                             history: List[HistoryEntry]) {
  def turnsUntilDraw: Option[Int] = drawStatus match {
    case DrawProposed(_, endTurnIndex) => Some(endTurnIndex - turnIndex)
    case _ => None
  }

  def wasDrawProposedBy(color: Color): Boolean = drawStatus match {
    case DrawProposed(c, _) if c == color => true
    case _ => false
  }

  def acceptDraw: GameState[DS, LS] = {
    val entry = HistoryEntry(turnIndex, turnToMove, board, drawStatus, Play.AcceptDraw)
    copy(turnIndex = turnIndex + 1,
      turnToMove = OPPONENT(turnToMove),
      history = entry :: history)
  }

  /**
    * For plays that don't end the current turn
    */
  def applyPartialPlay(play: Play, newBoard: BoardState, newDrawStatus: DrawStatus): GameState[DS, LS] = {
    val entry = HistoryEntry(turnIndex, turnToMove, board, drawStatus, play)
    copy(board = newBoard, drawStatus = newDrawStatus, history = entry :: history)
  }

  /**
    * For plays that end the current turn
    */
  def applyPlay(play: Play, newBoard: BoardState, newDrawStatus: DrawStatus): GameState[DS, LS] = {
    val entry = HistoryEntry(turnIndex, turnToMove, board, drawStatus, play)
    copy(turnIndex = turnIndex + 1,
      turnToMove = OPPONENT(turnToMove),
      board = newBoard,
      drawStatus = newDrawStatus,
      history = entry :: history)
  }

}


object GameState {
//  def create[DS, LS](config: GameConfig[DS, LS]): GameState[DS, LS] = {
//    val darkState = config.darkPlayer.initialState
//    val lightState = config.lightPlayer.initialState
//    val turnToMove = config.rulesSettings.playsFirst
//    val boardState = RulesSettings.initialBoard(config.rulesSettings)
//    GameState(config, boardState, turnToMove, 0, darkState, lightState, NoDraw, Nil)
//  }
}