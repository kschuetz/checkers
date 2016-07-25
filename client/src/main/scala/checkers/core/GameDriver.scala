package checkers.core

import checkers.consts._
import checkers.core.BeginTurnEvaluation._
import checkers.core.Phase.GameStart
import checkers.models.{BoardOrientation, GameModel}


class GameDriver[DS, LS](gameLogicModule: GameLogicModule)
                        (playerConfig: PlayerConfig[DS, LS]) {

  private val rulesSettings = gameLogicModule.rulesSettings
  private val moveGenerator = gameLogicModule.moveGenerator
  private val moveTreeFactory = gameLogicModule.moveTreeFactory

  def createInitialModel: GameModel[DS, LS] = {
    val gameState = createInitialState
    GameModel(
      nowTime = 0d,
      phase = GameStart,
      gameState = gameState,
      boardOrientation = BoardOrientation.Normal,
      ghostPiece = None,
      highlightedSquares = Set.empty,
      flipAnimation = None,
      animations = List.empty)
  }

  private def createInitialState: GameState[DS, LS] = {
    val darkState = playerConfig.darkPlayer.initialState
    val lightState = playerConfig.lightPlayer.initialState
    val turnToMove = rulesSettings.playsFirst
    val boardState = RulesSettings.initialBoard(rulesSettings)
    val beginTurnState = BeginTurnState(boardState, turnToMove, 0, NoDraw)
    val turnEvaluation = evaluateBeginTurn(beginTurnState)
    GameState(rulesSettings, playerConfig, boardState, turnToMove, 0, darkState, lightState, NoDraw, turnEvaluation, Nil)
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


class GameDriverFactory(gameLogicModule: GameLogicModule) {
  def createGameDriver[DS, LS](playerConfig: PlayerConfig[DS, LS]): GameDriver[DS, LS] = {
    new GameDriver(gameLogicModule)(playerConfig)
  }
}