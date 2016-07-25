package checkers.core

import checkers.consts._
import checkers.core.BeginTurnEvaluation._


class GameDriver(rulesSettings: RulesSettings,
                 moveGenerator: MoveGenerator,
                 moveTreeFactory: MoveTreeFactory) {

  def create[DS, LS](config: GameConfig[DS, LS]): GameState[DS, LS] = {
    val darkState = config.darkPlayer.initialState
    val lightState = config.lightPlayer.initialState
    val turnToMove = config.rulesSettings.playsFirst
    val boardState = RulesSettings.initialBoard(config.rulesSettings)
    val beginTurnState = BeginTurnState(boardState, turnToMove, 0, NoDraw)
    GameState(config, boardState, turnToMove, 0, darkState, lightState, NoDraw, Nil)
  }


  private def evaluateBeginTurn(beginTurnState: BeginTurnState): BeginTurnEvaluation = {
    if (beginTurnState.turnsUntilDraw.exists(_ <= 0)) Draw
    else {
      val turnToMove = beginTurnState.turnToMove
      val boardStack = BoardStack.fromBoard(beginTurnState.board)
      val moveList = moveGenerator.generateMoves(boardStack, beginTurnState.turnToMove)
      val moveTree = moveTreeFactory.fromMoveList(moveList)
      if (moveTree.isEmpty) {
        if (rulesSettings.giveaway) Win(turnToMove)
        else Win(OPPONENT(turnToMove))
      } else {
        CanMove(moveTree)
      }
    }
  }

}