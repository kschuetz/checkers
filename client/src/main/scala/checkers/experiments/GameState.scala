package checkers.experiments

import checkers.consts._
import checkers.core.{BoardState, Play, RulesSettings}


sealed trait DrawStatus
case object NoDraw extends DrawStatus
case class DrawProposed(color: Color, turnIndex: Int) extends DrawStatus

case class GameState[DS, LS](config: GameConfig[DS, LS],
                             boardState: BoardState,
                             turnToMove: Color,
                             turnIndex: Int,
                             darkState: DS,
                             lightState: LS,
                             drawStatus: DrawStatus,
                             moveHistory: List[Play],
                             history: List[GameState[DS, LS]])


object GameState {
  def create[DS, LS](config: GameConfig[DS, LS]): GameState[DS, LS] = {
    val darkState = config.darkPlayer.initialState
    val lightState = config.lightPlayer.initialState
    val turnToMove = config.rulesSettings.playsFirst
    val boardState = RulesSettings.initialBoard(config.rulesSettings)
    GameState(config, boardState, turnToMove, 0, darkState, lightState, NoDraw, Nil, Nil)
  }
}