package checkers.core

import checkers.consts._


sealed trait DrawStatus
case object NoDraw extends DrawStatus
case class DrawProposed(color: Color, endTurnIndex: Int) extends DrawStatus

case class GameState[DS, LS](config: GameConfig[DS, LS],
                             board: BoardState,
                             turnToMove: Color,
                             turnIndex: Int,
                             darkState: DS,
                             lightState: LS,
                             drawStatus: DrawStatus,
                             playHistory: List[Play],
                             boardHistory: List[BoardState],
                             history: List[GameState[DS, LS]]) {
  def turnsUntilDraw: Option[Int] = drawStatus match {
    case DrawProposed(_, endTurnIndex) => Some(endTurnIndex - turnIndex)
    case _ => None
  }
}


object GameState {
  def create[DS, LS](config: GameConfig[DS, LS]): GameState[DS, LS] = {
    val darkState = config.darkPlayer.initialState
    val lightState = config.lightPlayer.initialState
    val turnToMove = config.rulesSettings.playsFirst
    val boardState = RulesSettings.initialBoard(config.rulesSettings)
    GameState(config, boardState, turnToMove, 0, darkState, lightState, NoDraw, Nil, Nil, Nil)
  }
}