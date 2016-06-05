package checkers.game


case class RulesSettings(playsFirst: Color,
                         giveaway: Boolean)


object RulesSettings {

  val default = RulesSettings(
    playsFirst = Dark,
    giveaway = false)

  def initialBoard(settings: RulesSettings): BoardState = {
    val board = BoardState.empty
        .updateMany(LightMan)(Board.lightStartingSquares)
        .updateMany(DarkMan)(Board.darkStartingSquares)

    board
  }

  def initialGameState(settings: RulesSettings): GameState =
    GameState(initialBoard(settings), settings.playsFirst)
}