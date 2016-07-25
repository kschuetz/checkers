package checkers.models

import checkers.consts._
import checkers.core.Phase.GameStart
import checkers.core._
import checkers.models.Animation.{FlippingBoardAnimation, MovingPiece}

trait GameModelReader {
  def nowTime: Double

  def phase: Phase

  def ruleSettings: RulesSettings

  def darkPlayer: PlayerDescription

  def lightPlayer: PlayerDescription

  def board: BoardState

  def turnToMove: Color

  def turnIndex: Int

  def drawStatus: DrawStatus

  def history: List[HistoryEntry]

  def boardOrientation: BoardOrientation

  def ghostPiece: Option[GhostPiece]

  def clickableSquares: Set[Int]

  def highlightedSquares: Set[Int]

  def flipAnimation: Option[FlippingBoardAnimation]

  def animations: List[Animation]

  def getBoardRotation: Double
}

case class GameModel[DS, LS](nowTime: Double,
                             phase: Phase,
                             gameState: GameState[DS, LS],
                             boardOrientation: BoardOrientation,
                             ghostPiece: Option[GhostPiece],
                             highlightedSquares: Set[Int],
                             flipAnimation: Option[FlippingBoardAnimation],
                             animations: List[Animation]) extends GameModelReader {
  def hasActiveAnimations: Boolean =
    animations.exists(_.isActive(nowTime)) || flipAnimation.exists(_.isActive(nowTime))

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

  override def ruleSettings: RulesSettings = gameState.config.rulesSettings

  override def turnToMove: Color = gameState.turnToMove

  override def turnIndex: Int = gameState.turnIndex

  override def history: List[HistoryEntry] = gameState.history

  override def board: BoardState = gameState.board

  override def darkPlayer: PlayerDescription = gameState.config.darkPlayer

  override def lightPlayer: PlayerDescription = gameState.config.lightPlayer

  override def drawStatus: DrawStatus = gameState.drawStatus

  lazy val clickableSquares: Set[Int] = phase.clickableSquares

}

