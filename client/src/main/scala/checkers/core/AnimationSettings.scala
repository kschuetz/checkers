package checkers.core

trait AnimationSettings {
  def RemovePieceDurationMillis: Double
  def RemovePieceHumanDelayMillis: Double
  def RemovePieceComputerDelayMillis: Double
  def MovePieceDurationMillis: Double
  def JumpPieceDurationMillis: Double
  def ComputerMoveDelayMillis: Double
}



object DefaultAnimationSettings extends AnimationSettings {
  val RemovePieceDurationMillis = 330.0
  val RemovePieceHumanDelayMillis = 115.0
  val RemovePieceComputerDelayMillis = 500.0
  val MovePieceDurationMillis = 900.0
  val JumpPieceDurationMillis = 900.0
  val ComputerMoveDelayMillis = 750.0
}