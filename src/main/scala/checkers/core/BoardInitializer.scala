package checkers.core

import checkers.consts.{DARKMAN, LIGHTMAN}

trait BoardInitializer {
  def initialBoard(rulesSettings: RulesSettings): BoardState
}

object DefaultBoardInitializer extends BoardInitializer {
  def initialBoard(settings: RulesSettings): BoardState = {
    val board = BoardState.empty
      .updateMany(LIGHTMAN)(Board.lightStartingSquares)
      .updateMany(DARKMAN)(Board.darkStartingSquares)

    board
  }
}

class InitializerFromBoard(board: BoardStateRead) extends BoardInitializer {
  def initialBoard(rulesSettings: RulesSettings): BoardState = BoardState.create(board)
}