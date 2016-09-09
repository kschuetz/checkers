package checkers.core

import checkers.consts._
import checkers.core.Variation.Giveaway

case class RulesSettings(playsFirst: Color,
                         variation: Variation) {
  val giveaway = variation == Giveaway
}


object RulesSettings {

  val default = RulesSettings(
    playsFirst = DARK,
    variation = Variation.Standard)

  def initialBoard(settings: RulesSettings): BoardState = {
    val board = BoardState.empty
        .updateMany(LIGHTMAN)(Board.lightStartingSquares)
        .updateMany(DARKMAN)(Board.darkStartingSquares)

    board
  }

}