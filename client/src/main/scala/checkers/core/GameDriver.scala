package checkers.core

import checkers.userinterface.BoardMouseEvent
import checkers.computer.{PlayInput, PlayResult}
import checkers.consts._
import checkers.core.BeginTurnEvaluation._
import checkers.core.InputPhase._
import checkers.logger
import checkers.util.Point


class GameDriver(gameLogicModule: GameLogicModule)
                (playerConfig: PlayerConfig,
                 mentorConfig: MentorConfig) {

  private val log = logger.gameDriver

  type Model = GameModel
  type State = GameState

  private val rulesSettings = gameLogicModule.rulesSettings
  private val moveGenerator = gameLogicModule.moveGenerator
  private val moveTreeFactory = gameLogicModule.moveTreeFactory
  private val moveExecutor = gameLogicModule.moveExecutor
  private val drawLogic = gameLogicModule.drawLogic
  private val animationPlanner = gameLogicModule.animationPlanner
  private val evaluator = gameLogicModule.evaluator

  def createInitialModel(nowTime: Double): Model = {
    val gameState = createInitialState
    val snapshot = createSnapshot(gameState)
    val model = {
      val m1 = GameModel(
        nowTime = nowTime,
        gameStartTime = nowTime,
        turnStartTime = nowTime,
        inputPhase = GameStart(gameState),
        gameState = gameState,
        currentTurnSnapshot = snapshot,
        hintState = NullHintState,
        boardOrientation = BoardOrientation.Normal,
        pickedUpPiece = None,
        squareAttributesVector = SquareAttributesVector.default,
        gameLogUpdateId = 1,
        animation = AnimationModel.empty)

      schedulePlacePieces(m1)
    }

    model
  }

  def rotateBoard(model: Model): Option[Model] = {
    if (model.animation.rotate.nonEmpty) None // ignore if flip is already in progress
    else {
      val target = model.boardOrientation.opposite
      val anim = animationPlanner.createBoardRotateAnimation(model.nowTime)
      val animModel = model.animation.copy(rotate = Some(anim))
      logger.animations.info(s"rotating board: $anim")
      Some(model.copy(boardOrientation = target, animation = animModel))
    }
  }

  def userShowHint(model: Model): Option[Model] = {
    if(model.animation.isShowingHint) None
    else model.hintState match {
      case MentorAvailable(mentor, mentorOpaque) =>
        val playInput = getPlayInput(model.gameState)
        val computation = mentor.play(mentorOpaque, playInput)
        val newHintState = ComputingHint(model.nowTime, model.turnToMove, computation)
        Some(model.copy(hintState = newHintState))
      case answer: MentorAnswer =>
        scheduleHintAnimation(model, answer)
      case _ => None
    }
  }

  private def schedulePlacePieces(model: Model): Model = {
    val input = PlacePiecesAnimationInput(model.nowTime, model.animation, model.board)
    animationPlanner.placeAllPieces(input).fold(model)(model.withAnimationModel)
  }

  private def applyPlay(gameModel: Model, play: Play): Option[(PlayEvents, Model)] = {
    val gameState = gameModel.gameState

    log.debug(s"applying play: $play, tree = ${gameState.moveTree}")

    play match {
      case Play.NoPlay => None

//      case Play.AcceptDraw =>
//        if (drawLogic.canAcceptDraw(gameState)) {
//          val newState = gameState.acceptDraw(gameModel.currentTurnSnapshot)
//          val newModel = gameModel.copy(gameState = newState, inputPhase = InputPhase.GameOver(None))
//          Some((PlayEvents.acceptedDraw, newModel))
//
//        } else None

      case move: Play.Move =>
        gameState.moveTree.walk(move.path).map { case (endSquare, newMoveTree) =>
          log.debug(s"walk:  $newMoveTree")
          val remainingMoveTree =
            if (newMoveTree.isEmpty) newMoveTree
            else newMoveTree.prepend(endSquare)
          applyMove(gameModel, move, remainingMoveTree)
        }
    }
  }

  private def applyMove(gameModel: Model, move: Play.Move, remainingMoveTree: MoveTree): (PlayEvents, Model) = {
    val gameState = gameModel.gameState
    val boardState = gameState.board.toMutable
    val endsTurn = remainingMoveTree.isEmpty
    val isComputerPlayer = gameState.currentPlayer.isComputer

    log.debug(s"applyMove: remaining tree = $remainingMoveTree")

    def go(path: List[Int], result: List[MoveInfo]): List[MoveInfo] = {
      path match {
        case Nil => result
        case _ :: Nil => result
        case from :: (more@(to :: _)) =>
          val info = moveExecutor.execute(boardState, from, to)
          go(more, info :: result)
      }
    }

    val moveInfo = go(move.path, Nil).reverse
    val newBoard = boardState.toImmutable

    val entry = HistoryEntry(gameModel.currentTurnSnapshot, move)
    val newGameState = if (endsTurn) {
      val beginTurnState = BeginTurnState(board = newBoard,
        turnIndex = gameState.turnIndex + 1,
        turnToMove = OPPONENT(gameState.turnToMove),
        drawStatus = gameState.drawStatus)
      val turnEvaluation = evaluateBeginTurn(beginTurnState)
      log.info(turnEvaluation.toString)
      gameState.copy(board = newBoard,
        turnIndex = beginTurnState.turnIndex,
        turnToMove = beginTurnState.turnToMove,
        drawStatus = beginTurnState.drawStatus,
        beginTurnEvaluation = turnEvaluation,
        history = entry :: gameState.history)
    } else {
      // partial
      log.debug("partial move, updating tree:")
      log.debug(remainingMoveTree.toString)
      log.debug("----")

      val turnEvaluation = CanMove(remainingMoveTree)
      gameState.copy(board = newBoard, beginTurnEvaluation = turnEvaluation, history = entry :: gameState.history)
    }

    log.info(s"endsTurn: $endsTurn")

    val playEvents = if (endsTurn) PlayEvents.turnEnded else PlayEvents.partialTurn(remainingMoveTree)
    val newModel = {
      val m1 = gameModel.copy(gameState = newGameState, gameLogUpdateId = gameModel.gameLogUpdateId + 1)
      log.info(newGameState.board.toString)
      scheduleMoveAnimations(m1, moveInfo, isComputerPlayer)
    }

    (playEvents, newModel)
  }

  private def createInitialPlayerState(side: Side): PlayerState = {
    val mentorOpaque = mentorConfig.getMentor(side).map(_.initialOpaque)
    val playerOpaque = playerConfig.getPlayer(side).initialOpaque
    PlayerState(playerOpaque, mentorOpaque, 0)
  }

  private def createInitialState: State = {
    val darkState = createInitialPlayerState(DARK)
    val lightState = createInitialPlayerState(LIGHT)
    val turnToMove = rulesSettings.playsFirst
    val boardState = gameLogicModule.boardInitializer.initialBoard(rulesSettings)
    val beginTurnState = BeginTurnState(boardState, turnToMove, 0, NoDraw)
    val turnEvaluation = evaluateBeginTurn(beginTurnState)
    GameState(rulesSettings, playerConfig, mentorConfig, 0, boardState, turnToMove, 0, darkState, lightState,
      NoDraw, turnEvaluation, Nil)
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
        val targetSquares = moveTree.targetSquares(ps.square)
        sourceSquares ++ targetSquares
      case _ => Set.empty
    }
  }

  private def getInputPhase(nowTime: Double, player: Player, playerState: Opaque, playInput: => PlayInput): InputPhase = {
    player match {
      case Human => BeginHumanTurn
      case computer: Computer =>
        val playComputation = computer.program.play(playerState, playInput)
        ComputerThinking(nowTime, playComputation)
    }
  }

  private def initHintState(gameModel: Model): Model = {
    val hintState = if(gameModel.inputPhase.waitingForHuman) {
      gameModel.gameState.currentMentor.fold[HintState](NoMentorAvailable) {
        case (mentor, mentorOpaque) => MentorAvailable(mentor, mentorOpaque)
      }
    } else NoMentorAvailable
    gameModel.copy(hintState = hintState)
  }


  private def initTurn(gameModel: Model, newState: State): Model = {
    val model1 = restartTurn(gameModel, newState).copy(turnStartTime = gameModel.nowTime)
    initHintState(model1)
  }

  /**
    * Starts a new turn, but waits until play animations are completed
    */
  private def endTurn(gameModel: Model, nextTurnState: State): Model = {
    gameModel.copy(inputPhase = EndingTurn(nextTurnState), pickedUpPiece = None)
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

    val inputPhase = newState.beginTurnEvaluation match {
      case Win(side) => GameOver(Some(side))
      case Draw => GameOver(None)
      case _ =>
        getInputPhase(nowTime, playerConfig.getPlayer(turnToMove), newState.playerOpaque(turnToMove),
          getPlayInput(newState))
    }

    val clickableSquares = getClickableSquares(inputPhase, newState.moveTree)
    log.info(s"turnToMove: $turnToMove")
    log.info(s"moveTree:  ${newState.moveTree}")
    log.info(s"clickable: $clickableSquares")
    val squareAttributesVector = gameModel.squareAttributesVector.withClickable(clickableSquares).withGhost(Set.empty)

    val snapshot = createSnapshot(newState)

    gameModel.copy(inputPhase = inputPhase, gameState = newState, currentTurnSnapshot = snapshot,
      squareAttributesVector = squareAttributesVector, pickedUpPiece = None)
  }

  private def createSnapshot(gameState: GameState): Snapshot = {
    val (darkScore, lightScore) = evaluatePosition(gameState)
    Snapshot(
      gameClock = 0, //TODO
      turnIndex = gameState.turnIndex,
      turnToMove = gameState.turnToMove,
      board = gameState.board,
      drawStatus = gameState.drawStatus,
      darkState = gameState.darkState,
      lightState = gameState.lightState,
      darkScore = darkScore,
      lightScore = lightScore
    )
  }

  private def getPlayInput(gameState: State): PlayInput = {
    import gameState._
    PlayInput(board, gameState.turnIndex, gameState.rulesSettings, turnToMove, drawStatus, history)
  }

  def handleBoardMouseDown(model: Model, event: BoardMouseEvent): Option[Model] = {
    if (event.reactEvent.altKey) cycleOccupant(model, event.squareIndex, event.reactEvent.shiftKey, event.reactEvent.ctrlKey)
    else model.inputPhase match {
      case BeginHumanTurn => userSelectPiece(model, event.squareIndex, event.piece, Some(event.boardPoint))
      case PieceSelected(piece, squareIndex, validTargetSquares, canCancel) =>
        val targetSquare = event.squareIndex
        if (validTargetSquares.contains(targetSquare)) {
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

  def processComputations(model: Model): Option[Model] = {
   processComputerPlayerMoves(model) match {
     case result @ Some(model1) => processHints(model1).orElse(result)
     case None => processHints(model)
   }
  }

  private def processComputerPlayerMoves(model: Model): Option[Model] = {
    model.inputPhase match {
      case ct: ComputerThinking =>
        if (ct.playComputation.isReady) {
          val PlayResult(play, newPlayerState) = ct.playComputation.result

          val newGameState = model.gameState.withOpaque(model.gameState.turnToMove, newPlayerState)

          val newModel = model.copy(gameState = newGameState)
          applyPlay(newModel, play).map { case (playEvents, result) =>
            // Computer doesn't make partial moves, so play always ends turn
            endTurn(result, result.gameState)
          }

        } else None
      case _ => None
    }
  }

  private def processHints(model: Model): Option[Model] = {
    model.hintState match {
      case ch: ComputingHint if ch.playComputation.isReady =>
        val PlayResult(play, newOpaque) = ch.playComputation.result

        val newGameState = model.gameState.withMentorOpaque(ch.side, newOpaque)
        val answer = Hint.fromPlay(play).fold[MentorAnswer](NoSuggestion)(HintAvailable)
        val newModel = model.copy(gameState = newGameState, hintState = answer)

        log.info(s"Mentor provided hint: $answer")

        scheduleHintAnimation(newModel, answer).orElse(Some(newModel))
      case _ => None
    }
  }

  def rushComputer(model: Model): Option[Model] = {
    model.inputPhase match {
      case ct: ComputerThinking =>
        if(!ct.playComputation.isReady) {
          log.info("Rushing computer")
          ct.playComputation.rush()
          Some(model)
        } else None
      case _ => None
    }
  }

  def handleAnimationsComplete(model: Model): Option[Model] = {
    log.debug("handleAnimationsComplete")
    model.inputPhase match {
      case GameStart(nextTurnState) =>
        Some(initTurn(model, nextTurnState))
      case EndingTurn(nextTurnState) =>
        val turnTime = model.currentTurnClock
        val wasTurn = nextTurnState.opponent
        val newState = nextTurnState.addToClock(wasTurn, turnTime)
        Some(initTurn(model, newState))
      case _ => None
    }
  }

  private def userSelectPiece(model: Model, squareIndex: Int, piece: Occupant, clickPoint: Option[Point]): Option[Model] = {
    model.gameState.moveTree.down(squareIndex).map { moveTree =>
      log.debug(moveTree.toString)
      selectPiece(model, moveTree.squares, squareIndex, piece, clickPoint, canCancel = true)
    }.orElse(handleIllegalPieceSelection(model, squareIndex, piece))
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
      if (playEvents.endedTurn) endTurn(result, result.gameState)
      else {
        val nextMoveTree = playEvents.remainingMoveTree.next(toSquare)
        val validTargetSquares = nextMoveTree.squares
        continueTurn(result, toSquare, event.piece, validTargetSquares)
      }
    }
  }

  private def scheduleMoveAnimations(model: Model, moveInfo: List[MoveInfo], isComputerPlayer: Boolean): Model = {
    val currentPlayer = model.gameState.currentPlayer
    val input = MoveAnimationPlanInput(nowTime = model.nowTime, animationModel = model.animation,
      isComputerPlayer = isComputerPlayer, moveInfo = moveInfo)
    animationPlanner.scheduleMoveAnimations(input).fold(model)(model.withAnimationModel)
  }

  private def scheduleHintAnimation(model: Model, hint: MentorAnswer): Option[Model] = {
    val animationInput = HintAnimationInput(nowTime = model.nowTime, animationModel = model.animation,
      hint = hint)

    animationPlanner.showHint(animationInput).map(model.withAnimationModel)
  }

  private def handleIllegalPieceSelection(model: Model, squareIndex: Int, piece: Occupant): Option[Model] = {
    if (ISEMPTY(piece)) None
    else Some({
      val input = IllegalPieceAnimationInput(nowTime = model.nowTime, animationModel = model.animation,
        piece = piece, squareIndex = squareIndex)
      animationPlanner.illegalPieceSelection(input).fold(model)(model.withAnimationModel)
    })

  }

  private def evaluatePosition(gameState: GameState): (Int, Int) = {
    if (gameState.turnToMove == DARK) {
      val score = evaluator.evaluate(DARK, gameState.board)
      (score, -score)
    } else {
      val score = evaluator.evaluate(LIGHT, gameState.board)
      (-score, score)
    }
  }

  def cycleOccupant(model: Model, squareIndex: Int, reverse: Boolean = false, clear: Boolean = false): Option[Model] = {
    if(!model.inputPhase.waitingForHuman) return None

    val gameState = model.gameState
    val current = gameState.board.getOccupant(squareIndex)
    val next = if (clear) EMPTY
    else if (!reverse) {
      if (current == EMPTY) DARKMAN
      else if (current == DARKMAN) DARKKING
      else if (current == DARKKING) LIGHTMAN
      else if (current == LIGHTMAN) LIGHTKING
      else EMPTY
    } else {
      if (current == EMPTY) LIGHTKING
      else if (current == LIGHTKING) LIGHTMAN
      else if (current == LIGHTMAN) DARKKING
      else if (current == DARKKING) DARKMAN
      else EMPTY
    }

    if(current == next) return None

    val newBoard = gameState.board.updated(squareIndex, next)

    val beginTurnState = BeginTurnState(board = newBoard,
      turnIndex = gameState.turnIndex,
      turnToMove = gameState.turnToMove,
      drawStatus = gameState.drawStatus)
    val turnEvaluation = evaluateBeginTurn(beginTurnState)

    val newState = gameState.copy(board = newBoard, beginTurnEvaluation = turnEvaluation)
    val newModel = restartTurn(model, newState)
    Some(newModel)
  }

}
