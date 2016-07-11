package checkers.core

import checkers.consts._

case class RulesSettings(playsFirst: Color,
                         giveaway: Boolean)


object RulesSettings {

  val default = RulesSettings(
    playsFirst = DARK,
    giveaway = false)

  def initialBoard(settings: RulesSettings): BoardState = {
    val board = BoardState.empty
        .updateMany(LIGHTMAN)(Board.lightStartingSquares)
        .updateMany(DARKMAN)(Board.darkStartingSquares)

    board
  }

  def initialGameState(settings: RulesSettings): OldGameState =
    OldGameState(initialBoard(settings), settings.playsFirst)
}