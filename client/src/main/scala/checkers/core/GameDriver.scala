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
  private val moveExecutor = gameLogicModule.moveExecutor
  private val drawLogic = gameLogicModule.drawLogic

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

  def applyPlay(gameModel: GameModel[DS, LS], play: Play): Option[(PlayEvents, GameModel[DS, LS])] = {
    val myself = gameModel.turnToMove
    val opponent = OPPONENT(myself)
    val gameState = gameModel.gameState

    play match {
      case Play.NoPlay => None

      case Play.AcceptDraw =>
        if(drawLogic.canAcceptDraw(gameState)) {
          val newState = gameState.acceptDraw
          val newModel = gameModel.copy(gameState = newState, phase = Phase.GameOver(None))
          Some((PlayEvents.acceptedDraw, newModel))

        } else None

      case Play.Move(path, proposeDraw) =>
        val boardState = gameState.board.toMutable

        def go(path: List[Int], result: List[MoveInfo]): List[MoveInfo] = {
          path match {
            case Nil => result
            case from :: (more@(to :: _)) =>
              val info = moveExecutor.execute(boardState, from, to)
              go(more, info :: result)
          }
        }
        val moveInfo = go(path, Nil)
        val newBoard = boardState.toImmutable
        ???
    }

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
