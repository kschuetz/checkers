package checkers.core

import checkers.core.Animation.{HintAnimation, RotatingBoardAnimation}

case class AnimationModel(play: List[Animation],
                          rotate: Option[RotatingBoardAnimation],
                          hint: Option[HintAnimation]) {

  def hasActivePlayAnimations(nowTime: Double): Boolean =
    play.exists(_.isActive(nowTime)) || hint.exists(_.isActive(nowTime))

  def hasActiveAnimations(nowTime: Double): Boolean =
    hasActivePlayAnimations(nowTime) || rotate.exists(_.isActive(nowTime))

  def updateNowTime(newTime: Double): AnimationModel = {
    val newPlay = play.filterNot(_.isExpired(newTime))
    val newRotate = rotate.filterNot(_.isExpired(newTime))
    val newHint = hint.filterNot(_.isExpired(newTime))
    AnimationModel(newPlay, newRotate, newHint)
  }

  def addPlayAnims(newAnims: List[Animation]): AnimationModel = {
    if(newAnims.isEmpty) this
    else copy(play = newAnims ++ play)
  }

  def addPlayAnim(newAnim: Animation): AnimationModel =
    copy(play = newAnim :: play)

  def isShowingHint: Boolean = hint.nonEmpty
}


object AnimationModel {
  val empty = AnimationModel(Nil, None, None)
}