package checkers.models

import checkers.core.{GameConfig, GameState, RulesSettings}
import checkers.models.Animation.{FlippingBoardAnimation, MovingPiece}

case class GameScreenModel[DS, LS](nowTime: Double,
                           gameState: GameState[DS, LS],
                           boardOrientation: BoardOrientation,
                           ghostPiece: Option[GhostPiece],
                           clickableSquares: Set[Int],
                           highlightedSquares: Set[Int],
                           flipAnimation: Option[FlippingBoardAnimation],
                           animations: List[Animation]) {
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

  def updateNowTime(newTime: Double): GameScreenModel[DS, LS] = {
    val newAnimations = animations.filterNot(_.isExpired(newTime))
    val newFlip = flipAnimation.filterNot(_.isExpired(newTime))
    copy(nowTime = newTime, animations = newAnimations, flipAnimation = newFlip)
  }

  def startFlipBoard(duration: Double): GameScreenModel[DS, LS] = {
    if(flipAnimation.nonEmpty) this           // ignore if flip is already in progress
    else {
      val target = boardOrientation.opposite
      val anim = FlippingBoardAnimation(nowTime, duration)
      copy(boardOrientation = target, flipAnimation = Some(anim))
    }
  }

  def startMovePiece(fromSquare: Int, toSquare: Int, duration: Double): GameScreenModel[DS, LS] = {
    val anim = MovingPiece(fromSquare, toSquare, nowTime, duration)
    copy(animations = anim :: animations)
  }

  def startJumpPath(path: Seq[Int], durationPerStep: Double): GameScreenModel[DS, LS] = {
    ???


  }

}


object GameScreenModel {

  def initial[DS, LS](config: GameConfig[DS, LS]): GameScreenModel[DS, LS] = {
    val gameState = GameState.create(config)
    GameScreenModel(
      nowTime = 0d,
      gameState = gameState,
      boardOrientation = BoardOrientation.Normal,
      ghostPiece = None,
      clickableSquares = Set.empty,
      highlightedSquares = Set.empty,
      flipAnimation = None,
      animations = List.empty)
  }


}