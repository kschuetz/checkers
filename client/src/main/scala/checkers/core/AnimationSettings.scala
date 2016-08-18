package checkers.core

trait AnimationSettings {
  def RemovePieceDurationMillis: Double
  def RemovePieceHumanDelayMillis: Double
  def MovePieceDurationMillis: Double
  def JumpPieceDurationMillis: Double
}



object DefaultAnimationSettings extends AnimationSettings {
  val RemovePieceDurationMillis = 1100.0
  val RemovePieceHumanDelayMillis = 200.0
  val MovePieceDurationMillis = 900.0
  val JumpPieceDurationMillis = 900.0
}