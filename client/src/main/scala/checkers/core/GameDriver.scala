package checkers.core

import checkers.computer.PlayInput
import checkers.consts._
import checkers.core.BeginTurnEvaluation._
import checkers.core.InputPhase.{BeginHumanTurn, ComputerThinking, GameStart}
import checkers.models.{BoardOrientation, GameModel}


class GameDriver[DS, LS](gameLogicModule: GameLogicModule)
                        (playerConfig: PlayerConfig[DS, LS]) {

  type Model = GameModel[DS, LS]
  type State = GameState[DS, LS]

  private val rulesSettings = gameLogicModule.rulesSettings
  private val moveGenerator = gameLogicModule.moveGenerator
  private val moveTreeFactory = gameLogicModule.moveTreeFactory
  private val moveExecutor = gameLogicModule.moveExecutor
  private val drawLogic = gameLogicModule.drawLogic

  def createInitialModel(nowTime: Double): Model = {
    val gameState = createInitialState
    val model = GameModel(
      nowTime = nowTime,
      gameStartTime = nowTime,
      turnStartTime = nowTime,
      inputPhase = BeginHumanTurn,
      gameState = gameState,
      boardOrientation = BoardOrientation.Normal,
      ghostPiece = None,
      clickableSquares = Set.empty,
      highlightedSquares = Set.empty,
      flipAnimation = None,
      animations = List.empty)
    initTurn(model, gameState)
  }

  def applyPlay(gameModel: Model, play: Play): Option[(PlayEvents, Model)] = {
    val myself = gameModel.turnToMove
    val opponent = OPPONENT(myself)
    val gameState = gameModel.gameState

    play match {
      case Play.NoPlay => None

      case Play.AcceptDraw =>
        if(drawLogic.canAcceptDraw(gameState)) {
          val newState = gameState.acceptDraw
          val newModel = gameModel.copy(gameState = newState, inputPhase = InputPhase.GameOver(None))
          Some((PlayEvents.acceptedDraw, newModel))

        } else None

      case move: Play.Move =>
        val moveTree = gameState.moveTree
        moveTree.walk(move.path).map { newMoveTree =>
          applyMove(gameModel, move, newMoveTree)
        }
    }
  }

  private def applyMove(gameModel: Model, move: Play.Move, newMoveTree: MoveTree): (PlayEvents, Model) = {
    val gameState = gameModel.gameState
    val boardState = gameState.board.toMutable
    val endsTurn = newMoveTree.isEmpty

    def go(path: List[Int], result: List[MoveInfo]): List[MoveInfo] = {
      path match {
        case Nil => result
        case _ :: Nil => result
        case from :: (more@(to :: _)) =>
          val info = moveExecutor.execute(boardState, from, to)
          go(more, info :: result)
      }
    }
    val moveInfo = go(move.path, Nil)
    val newBoard = boardState.toImmutable
    // TODO: schedule animations

    val entry = HistoryEntry(gameState.turnIndex, gameState.turnToMove, gameState.board, gameState.drawStatus, move)
    val newGameState = if(endsTurn) {
      val beginTurnState = BeginTurnState(board = newBoard,
        turnIndex = gameState.turnIndex + 1,
        turnToMove = OPPONENT(gameState.turnToMove),
        drawStatus = gameState.drawStatus)
      val turnEvaluation = evaluateBeginTurn(beginTurnState)
      gameState.copy(board = newBoard,
        turnIndex = beginTurnState.turnIndex,
        turnToMove = beginTurnState.turnToMove,
        drawStatus = beginTurnState.drawStatus,
        history = entry :: gameState.history)
    } else {
      // partial
      val turnEvaluation = CanMove(newMoveTree)
      gameState.copy(board = newBoard, history = entry :: gameState.history)
    }

    val playEvents = if(endsTurn) PlayEvents.turnEnded else PlayEvents.partialTurn
    val newModel = gameModel.copy(gameState = newGameState)
    (playEvents, newModel)
  }

  private def createInitialState: State = {
    val darkState = playerConfig.darkPlayer.initialState
    val lightState = playerConfig.lightPlayer.initialState
    val turnToMove = rulesSettings.playsFirst
    val boardState = RulesSettings.initialBoard(rulesSettings)
    val beginTurnState = BeginTurnState(boardState, turnToMove, 0, NoDraw)
    val turnEvaluation = evaluateBeginTurn(beginTurnState)
    GameState(rulesSettings, playerConfig, boardState, turnToMove, 0, darkState, lightState, NoDraw, turnEvaluation, 0, 0, Nil)
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

  private def getClickableSquares(inputPhase: InputPhase, moveTree: MoveTree): Set[Int] = {
    inputPhase match {
      case InputPhase.BeginHumanTurn => moveTree.squares
      case ps: InputPhase.PieceSelected =>
        val sourceSquares = moveTree.squares
        val destSquares = moveTree.walk(List(ps.square)).fold(Set.empty[Int])(_.squares)
        sourceSquares ++ destSquares
      case _ => Set.empty
    }
  }

  private def getInputPhase[A](nowTime: Double, player: Player[A], playerState: A, playInput: => PlayInput): InputPhase = {
    player match {
      case Human => BeginHumanTurn
      case Computer(program) =>
        val playComputation = program.play(playerState, playInput)
        ComputerThinking(nowTime, playComputation)
    }
  }

  private def initTurn(gameModel: Model, newState: State): Model = {
    val turnToMove = newState.turnToMove
    val nowTime = gameModel.nowTime
    val inputPhase = if(turnToMove == LIGHT) {
      getInputPhase(nowTime, playerConfig.lightPlayer, newState.lightState, getPlayInput(newState))
    } else {
      getInputPhase(nowTime, playerConfig.darkPlayer, newState.darkState, getPlayInput(newState))
    }

    val clickableSquares = getClickableSquares(inputPhase, newState.moveTree)

    gameModel.copy(inputPhase = inputPhase, turnStartTime = nowTime, gameState = newState, clickableSquares = clickableSquares)
  }

  private def getPlayInput(gameState: State): PlayInput = {
    import gameState._
    PlayInput(board, gameState.rulesSettings, turnToMove, drawStatus, history)
  }

}
