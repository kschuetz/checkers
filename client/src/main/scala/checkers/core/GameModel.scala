package checkers.core

import checkers.consts._
import checkers.core.InputPhase.ComputerThinking
import checkers.util.Easing

trait GameModelReader {
  def nowTime: Double

  def inputPhase: InputPhase

  def ruleSettings: RulesSettings

  def darkPlayer: PlayerDescription

  def lightPlayer: PlayerDescription

  def board: BoardState

  def turnToMove: Color

  // may differ from turnToMove due to waiting for animations
  def displayTurnToMove: Color

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

  def scoreDisplayEnabled: Boolean

  def getScore(color: Color): Int
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
                     animation: AnimationModel) extends GameModelReader {

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

  def withAnimationModel(newAnimationModel: AnimationModel) = copy(animation = newAnimationModel)

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

  override def ruleSettings: RulesSettings = gameState.rulesSettings

  override def turnToMove: Color = gameState.turnToMove

  override def displayTurnToMove: Color =
    if(inputPhase.endingTurn) OPPONENT(gameState.turnToMove)
    else gameState.turnToMove

  override def turnIndex: Int = gameState.turnIndex

  override def history: List[HistoryEntry] = gameState.history

  override def board: BoardState = gameState.board

  override def darkPlayer: PlayerDescription = gameState.playerConfig.darkPlayer

  override def lightPlayer: PlayerDescription = gameState.playerConfig.lightPlayer

  override def drawStatus: DrawStatus = gameState.drawStatus

  override def canClickPieces: Boolean = pickedUpPiece.isEmpty

  override def squareAttributes: Vector[SquareAttributes] = squareAttributesVector.items

  override def playerMustJump: Boolean = gameState.beginTurnEvaluation.requiresJump

  override def scoreDisplayEnabled: Boolean = true

  override def getScore(color: Color): Int = if(color == DARK) darkScore else lightScore
}

