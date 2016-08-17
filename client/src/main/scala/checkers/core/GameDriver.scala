package checkers.core

import checkers.components.BoardMouseEvent
import checkers.computer.PlayInput
import checkers.consts._
import checkers.core.BeginTurnEvaluation._
import checkers.core.InputPhase.{BeginHumanTurn, ComputerThinking, PieceSelected}
import checkers.geometry.Point
import checkers.models.{BoardOrientation, GameModel, PickedUpPiece, SquareAttributesVector}
import checkers.test.BoardExperiments


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
      pickedUpPiece = None,
      squareAttributesVector = SquareAttributesVector.default,
      flipAnimation = None,
      animations = List.empty)
    initTurn(model, gameState)
  }

  private def applyPlay(gameModel: Model, play: Play): Option[(PlayEvents, Model)] = {
    val gameState = gameModel.gameState

    println(s"applying play: $play, tree = ${gameState.moveTree}")

    play match {
      case Play.NoPlay => None

      case Play.AcceptDraw =>
        if (drawLogic.canAcceptDraw(gameState)) {
          val newState = gameState.acceptDraw
          val newModel = gameModel.copy(gameState = newState, inputPhase = InputPhase.GameOver(None))
          Some((PlayEvents.acceptedDraw, newModel))

        } else None

//      case move: Play.Move =>
//        gameState.moveTree.walk(move.path).map { newMoveTree =>
//          applyMove(gameModel, move, newMoveTree)
//        }
      case move: Play.Move =>
        gameState.moveTree.walk(move.path).map { case (endSquare, newMoveTree) =>
          println(s"walk2:  $newMoveTree")
          val remainingMoveTree =
            if(newMoveTree.isEmpty) newMoveTree
            else {
              MoveTree.singleton(endSquare, newMoveTree)
            }
          applyMove(gameModel, move, remainingMoveTree)
        }
    }
  }

  private def applyMove(gameModel: Model, move: Play.Move, remainingMoveTree: MoveTree): (PlayEvents, Model) = {
    val gameState = gameModel.gameState
    val boardState = gameState.board.toMutable
    val endsTurn = remainingMoveTree.isEmpty

    println(s"applyMove: remaining tree = $remainingMoveTree")

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
    val newGameState = if (endsTurn) {
      val beginTurnState = BeginTurnState(board = newBoard,
        turnIndex = gameState.turnIndex + 1,
        turnToMove = OPPONENT(gameState.turnToMove),
        drawStatus = gameState.drawStatus)
      val turnEvaluation = evaluateBeginTurn(beginTurnState)
      println(turnEvaluation)
      gameState.copy(board = newBoard,
        turnIndex = beginTurnState.turnIndex,
        turnToMove = beginTurnState.turnToMove,
        drawStatus = beginTurnState.drawStatus,
        beginTurnEvaluation = turnEvaluation,
        history = entry :: gameState.history)
    } else {
      // partial
      println("partial move, updating tree:")
      println(remainingMoveTree)
      println("----")

      val turnEvaluation = CanMove(remainingMoveTree)
      gameState.copy(board = newBoard, beginTurnEvaluation = turnEvaluation, history = entry :: gameState.history)
    }

    println(s"endsTurn: $endsTurn")

    val playEvents = if (endsTurn) PlayEvents.turnEnded else PlayEvents.partialTurn(remainingMoveTree)
    val newModel = gameModel.copy(gameState = newGameState)
    (playEvents, newModel)
  }

  private def createInitialState: State = {
    val darkState = playerConfig.darkPlayer.initialState
    val lightState = playerConfig.lightPlayer.initialState
    val turnToMove = rulesSettings.playsFirst
    //val boardState = RulesSettings.initialBoard(rulesSettings)
    val boardState = BoardExperiments.board1
    val beginTurnState = BeginTurnState(boardState, turnToMove, 0, NoDraw)
    val turnEvaluation = evaluateBeginTurn(beginTurnState)
    GameState(rulesSettings, playerConfig, boardState, turnToMove, 0, darkState, lightState, NoDraw, turnEvaluation, 0, 0, Nil)
  }

  private def evaluateBeginTurn(beginTurnState: BeginTurnState): BeginTurnEvaluation = {
    if (beginTurnState.turnsUntilDraw.exists(_ <= 0)) Draw
    else {
      val turnToMove = beginTurnState.turnToMove
      val boardStack = BoardStack.fromBoard(beginTurnState.board)
      val moveList = moveGenerator.generateMoves(boardStack, turnToMove)
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
//        val destSquares = moveTree.walk(List(ps.square)).fold(Set.empty[Int])(_.squares)
        val targetSquares  = moveTree.targetSquares(ps.square)
        sourceSquares ++ targetSquares
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
    restartTurn(gameModel, newState).copy(turnStartTime = gameModel.nowTime)
  }

  private def continueTurn(model: Model, fromSquare: Int, piece: Occupant, validTargetSquares: Set[Int]): Model = {
    selectPiece(model, validTargetSquares, fromSquare, piece, None, canCancel = false)
  }

  /**
    * Reverts back to beginning of current turn (e.g, in case of cancelling a piece selection),
    * but does not update the turnStartTime.
    */
  private def restartTurn(gameModel: Model, newState: State): Model = {
    val turnToMove = newState.turnToMove
    val nowTime = gameModel.nowTime
    val inputPhase = if (turnToMove == LIGHT) {
      getInputPhase(nowTime, playerConfig.lightPlayer, newState.lightState, getPlayInput(newState))
    } else {
      getInputPhase(nowTime, playerConfig.darkPlayer, newState.darkState, getPlayInput(newState))
    }

    val clickableSquares = getClickableSquares(inputPhase, newState.moveTree)
    println(s"turnToMove: $turnToMove")
    println(clickableSquares)
    val squareAttributesVector = gameModel.squareAttributesVector.withClickable(clickableSquares).withGhost(Set.empty)

    gameModel.copy(inputPhase = inputPhase, gameState = newState, squareAttributesVector = squareAttributesVector, pickedUpPiece = None)
  }

  private def getPlayInput(gameState: State): PlayInput = {
    import gameState._
    PlayInput(board, gameState.rulesSettings, turnToMove, drawStatus, history)
  }

  def handleBoardMouseDown(model: Model, event: BoardMouseEvent): Option[Model] = {
    model.inputPhase match {
      case BeginHumanTurn => userSelectPiece(model, event.squareIndex, event.piece, Some(event.boardPoint))
      case PieceSelected(piece, squareIndex, validTargetSquares, canCancel) =>
        val targetSquare = event.squareIndex
        if(validTargetSquares.contains(targetSquare)) {
          selectMoveTarget(model, event, squareIndex, targetSquare)
        } else if (canCancel) {
          Option(cancelPieceSelected(model))
        } else None
      case _ => None
    }
  }

  def handleBoardMouseMove(model: Model, event: BoardMouseEvent): Option[Model] = {
    model.inputPhase match {
      case PieceSelected(piece, squareIndex, _, _) =>
        val pickedUpPiece = PickedUpPiece(piece, squareIndex, event.boardPoint)
        val squareAttributesVector = model.squareAttributesVector.withGhost(Set(squareIndex))
        Some(model.copy(pickedUpPiece = Some(pickedUpPiece), squareAttributesVector = squareAttributesVector))
      case _ => None
    }
  }

  private def userSelectPiece(model: Model, squareIndex: Int, piece: Occupant, clickPoint: Option[Point]): Option[Model] = {
    model.gameState.moveTree.down(squareIndex).map { moveTree =>
      println(moveTree)
      selectPiece(model, moveTree.squares, squareIndex, piece, clickPoint, canCancel = true)
    }
  }

  private def selectPiece(model: Model, validTargetSquares: Set[Int], squareIndex: Int, piece: Occupant, clickPoint: Option[Point], canCancel: Boolean): Model = {
    val boardPoint = clickPoint.getOrElse(Board.squareCenter(squareIndex))
    val inputPhase = PieceSelected(piece, squareIndex, validTargetSquares, canCancel = canCancel)
    val pickedUpPiece = PickedUpPiece(piece, squareIndex, boardPoint)
    val squareAttributesVector = model.squareAttributesVector.withClickable(validTargetSquares).withGhost(Set(squareIndex))
    model.copy(inputPhase = inputPhase, pickedUpPiece = Some(pickedUpPiece), squareAttributesVector = squareAttributesVector)
  }

  private def cancelPieceSelected(model: Model): Model = {
    restartTurn(model, model.gameState)
  }

  private def selectMoveTarget(model: Model, event: BoardMouseEvent, fromSquare: Int, toSquare: Int): Option[Model] = {
    val play = Play.move(fromSquare, toSquare)
    applyPlay(model, play).map { case (playEvents, result) =>
      if (playEvents.endedTurn) initTurn(result, result.gameState)
      else {
        val nextMoveTree = playEvents.remainingMoveTree.next(toSquare)
        val validTargetSquares = nextMoveTree.squares
        continueTurn(result, toSquare, event.piece, validTargetSquares)
      }
    }
  }

}
