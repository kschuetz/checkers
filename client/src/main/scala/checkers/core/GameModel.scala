package checkers.core

import checkers.consts._
import checkers.core.InputPhase.{ComputerThinking, GameOver}
import checkers.util.Easing

trait GameModelReader {
  def nowTime: Double

  def inputPhase: InputPhase

  def ruleSettings: RulesSettings

  def darkPlayer: PlayerDescription

  def lightPlayer: PlayerDescription

  def board: BoardState

  def turnToMove: Side

  // may differ from turnToMove due to waiting for animations
  def displayTurnToMove: Side

  def turnIndex: Int

  def drawStatus: DrawStatus

  def history: List[HistoryEntry]

  def boardOrientation: BoardOrientation

  def pickedUpPiece: Option[PickedUpPiece]

  def squareAttributes: Vector[SquareAttributes]

  def animation: AnimationModel

  def getBoardRotation: Double

  def canClickPieces: Boolean

  def playerMustJump: Boolean

  def playerClock(side: Side): Double

  def currentTurnClock: Double

  def gameOverState: Option[GameOverState]

  def scoreDisplayEnabled: Boolean

  def getScore(side: Side): Int

  def clockDisplayHash: Int
}

case class GameModel(nowTime: Double,
                     gameStartTime: Double,
                     turnStartTime: Double,
                     inputPhase: InputPhase,
                     gameState: GameState,
                     darkScore: Int,
                     lightScore: Int,
                     boardOrientation: BoardOrientation,
                     pickedUpPiece: Option[PickedUpPiece],
                     squareAttributesVector: SquareAttributesVector,
                     animation: AnimationModel) extends GameModelReader with ComputationProcess {

  /**
    * Has any animations that affect game play (e.g. moving a piece)
    */
  def hasActivePlayAnimations: Boolean = animation.hasActivePlayAnimations(nowTime)

  def hasActiveAnimations: Boolean = animation.hasActiveAnimations(nowTime)

  def hasActiveComputation: Boolean = inputPhase.waitingForComputer

  def waitingForAnimations: Boolean = inputPhase.waitingForAnimations

  def runComputations(maxCycles: Int): Int = {
    inputPhase match {
      case ct: ComputerThinking =>
        ct.playComputation.run(maxCycles)
      case _ => 0
    }
  }

  def withAnimationModel(newAnimationModel: AnimationModel): GameModel = copy(animation = newAnimationModel)

  def getBoardRotation: Double = {
    val offset = animation.rotate.map { anim =>
      val t = anim.linearProgress(nowTime)
      val amount = 1.0 - Easing.easeInOutQuart(t)

      -180 * amount
    } getOrElse 0.0

    boardOrientation.angle + offset
  }

  def updateNowTime(newTime: Double): GameModel = {
    val newAnimation = animation.updateNowTime(newTime)
    copy(nowTime = newTime, animation = newAnimation)
  }

  def ruleSettings: RulesSettings = gameState.rulesSettings

  def turnToMove: Side = gameState.turnToMove

  def displayTurnToMove: Side =
    if(inputPhase.endingTurn) OPPONENT(gameState.turnToMove)
    else gameState.turnToMove

  def turnIndex: Int = gameState.turnIndex

  def history: List[HistoryEntry] = gameState.history

  def board: BoardState = gameState.board

  def darkPlayer: PlayerDescription = gameState.playerConfig.darkPlayer

  def lightPlayer: PlayerDescription = gameState.playerConfig.lightPlayer

  def drawStatus: DrawStatus = gameState.drawStatus

  def canClickPieces: Boolean = pickedUpPiece.isEmpty

  def squareAttributes: Vector[SquareAttributes] = squareAttributesVector.items

  def playerMustJump: Boolean = gameState.beginTurnEvaluation.requiresJump

  def scoreDisplayEnabled: Boolean = true

  def getScore(side: Side): Int = if(side == DARK) darkScore else lightScore

  def gameOverState: Option[GameOverState] = inputPhase match {
    case GameOver(winner) =>
      val result = winner.fold[GameOverState](GameOverState.Draw){ side =>
        val player = if(side == DARK) darkPlayer else lightPlayer
        GameOverState.Winner(side, player)
      }
      Some(result)
    case _ => None
  }

  def playerClock(side: Side): Double = {
    val base = if(side == DARK) gameState.darkClock else gameState.lightClock
    val addCurrentTurn = inputPhase.onTheClock &&
      ((gameState.turnToMove == side) != inputPhase.endingTurn)    // xor
    if (addCurrentTurn) base + currentTurnClock else base
  }

  def currentTurnClock: Double = nowTime - turnStartTime

  def clockDisplayHash: Int = {
    val dark = math.floor(playerClock(DARK) / 1000).toInt
    val light = math.floor(playerClock(LIGHT) / 1000).toInt
    (light << 16) | (dark & 0xFFFF)
  }
}

