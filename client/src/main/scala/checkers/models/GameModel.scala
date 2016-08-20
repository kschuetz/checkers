package checkers.models

import checkers.consts._
import checkers.core.InputPhase.ComputerThinking
import checkers.core._
import checkers.models.Animation.{FlippingBoardAnimation, MovingPiece}

trait GameModelReader {
  def nowTime: Double

  def inputPhase: InputPhase

  def ruleSettings: RulesSettings

  def darkPlayer: PlayerDescription

  def lightPlayer: PlayerDescription

  def board: BoardState

  def turnToMove: Color

  def turnIndex: Int

  def drawStatus: DrawStatus

  def history: List[HistoryEntry]

  def boardOrientation: BoardOrientation

  def pickedUpPiece: Option[PickedUpPiece]

  def squareAttributes: Vector[SquareAttributes]

  def flipAnimation: Option[FlippingBoardAnimation]

  def animations: List[Animation]

  def getBoardRotation: Double

  def canClickPieces: Boolean
}

case class GameModel[DS, LS](nowTime: Double,
                             gameStartTime: Double,
                             turnStartTime: Double,
                             inputPhase: InputPhase,
                             gameState: GameState[DS, LS],
                             boardOrientation: BoardOrientation,
                             pickedUpPiece: Option[PickedUpPiece],
                             squareAttributesVector: SquareAttributesVector,
                             flipAnimation: Option[FlippingBoardAnimation],
                             animations: List[Animation]) extends GameModelReader {

  /**
    * Has any animations that affect game play (e.g. moving a piece)
    */
  def hasActivePlayAnimations: Boolean =
    animations.exists(_.isActive(nowTime))

  def hasActiveAnimations: Boolean =
    hasActivePlayAnimations || flipAnimation.exists(_.isActive(nowTime))

  def hasActiveComputation: Boolean = inputPhase.waitingForComputer

  def waitingForAnimations: Boolean = inputPhase.waitingForAnimations

  def runComputations(maxCycles: Int): Int = {
    inputPhase match {
      case ct: ComputerThinking[_] =>
        ct.playComputation.run(maxCycles)
      case _ => 0
    }
  }

  def getBoardRotation: Double = {
    // TODO: easing
    val offset = flipAnimation.map { anim =>
      val amount = 1.0 - anim.linearProgress(nowTime)
      180 * amount
    } getOrElse 0.0

    boardOrientation.angle + offset
  }

  def updateNowTime(newTime: Double): GameModel[DS, LS] = {
    val newAnimations = animations.filterNot(_.isExpired(newTime))
    val newFlip = flipAnimation.filterNot(_.isExpired(newTime))
    copy(nowTime = newTime, animations = newAnimations, flipAnimation = newFlip)
  }

  def startFlipBoard(duration: Double): GameModel[DS, LS] = {
    if (flipAnimation.nonEmpty) this // ignore if flip is already in progress
    else {
      val target = boardOrientation.opposite
      val anim = FlippingBoardAnimation(nowTime, duration)
      copy(boardOrientation = target, flipAnimation = Some(anim))
    }
  }

  def startMovePiece(fromSquare: Int, toSquare: Int, duration: Double): GameModel[DS, LS] = {
    val anim = MovingPiece(fromSquare, toSquare, nowTime, duration)
    copy(animations = anim :: animations)
  }

  def startJumpPath(path: Seq[Int], durationPerStep: Double): GameModel[DS, LS] = {
    ???


  }

  override def ruleSettings: RulesSettings = gameState.rulesSettings

  override def turnToMove: Color = gameState.turnToMove

  override def turnIndex: Int = gameState.turnIndex

  override def history: List[HistoryEntry] = gameState.history

  override def board: BoardState = gameState.board

  override def darkPlayer: PlayerDescription = gameState.playerConfig.darkPlayer

  override def lightPlayer: PlayerDescription = gameState.playerConfig.lightPlayer

  override def drawStatus: DrawStatus = gameState.drawStatus

  override def canClickPieces: Boolean = pickedUpPiece.isEmpty

  override def squareAttributes: Vector[SquareAttributes] = squareAttributesVector.items

}

