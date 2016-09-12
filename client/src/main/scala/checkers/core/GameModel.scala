package checkers.core

import checkers.consts._
import checkers.core.Animation.RotatingBoardAnimation
import checkers.core.InputPhase.ComputerThinking

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

  def rotateAnimation: Option[RotatingBoardAnimation]

  def animations: List[Animation]

  def getBoardRotation: Double

  def canClickPieces: Boolean

  def playerMustJump: Boolean
}

case class GameModel(nowTime: Double,
                     gameStartTime: Double,
                     turnStartTime: Double,
                     inputPhase: InputPhase,
                     gameState: GameState,
                     boardOrientation: BoardOrientation,
                     pickedUpPiece: Option[PickedUpPiece],
                     squareAttributesVector: SquareAttributesVector,
                     rotateAnimation: Option[RotatingBoardAnimation],
                     animations: List[Animation]) extends GameModelReader {

  /**
    * Has any animations that affect game play (e.g. moving a piece)
    */
  def hasActivePlayAnimations: Boolean =
    animations.exists(_.isActive(nowTime))

  def hasActiveAnimations: Boolean =
    hasActivePlayAnimations || rotateAnimation.exists(_.isActive(nowTime))

  def hasActiveComputation: Boolean = inputPhase.waitingForComputer

  def waitingForAnimations: Boolean = inputPhase.waitingForAnimations

  def runComputations(maxCycles: Int): Int = {
    inputPhase match {
      case ct: ComputerThinking =>
        ct.playComputation.run(maxCycles)
      case _ => 0
    }
  }

  def withNewAnimations(newAnimations: List[Animation]) = copy(animations = newAnimations)

  def getBoardRotation: Double = {
    // TODO: easing
    val offset = rotateAnimation.map { anim =>
      val amount = 1.0 - anim.linearProgress(nowTime)
//      if(amount != 0) println(s"rotation: $amount     at $nowTime")

      180 * amount
    } getOrElse 0.0

    boardOrientation.angle + offset
  }

  def updateNowTime(newTime: Double): GameModel = {
    val newAnimations = animations.filterNot(_.isExpired(newTime))
    val newFlip = rotateAnimation.filterNot(_.isExpired(newTime))

    copy(nowTime = newTime, animations = newAnimations, rotateAnimation = newFlip)
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

}

