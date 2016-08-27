package checkers.core

trait AnimationSettings {
  def ComputerMoveDelayMillis: Double

  def MovePieceDurationMillis: Double

  def JumpPieceDurationMillis: Double

  def RemovePieceDurationMillis: Double
  def RemovePieceHumanDelayMillis: Double
  def RemovePieceHumanIntervalMillis: Double
  def RemovePieceComputerDelayMillis: Double
  def RemovePieceComputerIntervalMillis: Double
}



object DefaultAnimationSettings extends AnimationSettings {
  val RemovePieceDurationMillis = 330.0
  val RemovePieceHumanDelayMillis = 115.0
  val RemovePieceHumanIntervalMillis = 115.0
  val MovePieceDurationMillis = 900.0
  val JumpPieceDurationMillis = 900.0
  val ComputerMoveDelayMillis = 750.0

  val RemovePieceComputerDelayMillis = JumpPieceDurationMillis * 0.67
  val RemovePieceComputerIntervalMillis = JumpPieceDurationMillis
}