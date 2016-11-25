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

  def IllegalPieceSelectionDurationMillis: Double
  
  def PlacePiecesTopDelayMillis: Double
  def PlacePiecesBottomDelayMillis: Double
  def PlacePiecesIntervalMillis: Double
  def PlacePieceDurationMillis: Double

  def BoardRotateDurationMillis: Double
}



object DefaultAnimationSettings extends AnimationSettings {
  val RemovePieceDurationMillis = 330.0
  val RemovePieceHumanDelayMillis = 115.0
  val RemovePieceHumanIntervalMillis = 115.0
  val MovePieceDurationMillis = 900.0
  val JumpPieceDurationMillis = 900.0
  val ComputerMoveDelayMillis = 750.0

  val RemovePieceComputerDelayMillis: Double = JumpPieceDurationMillis * 0.67
  val RemovePieceComputerIntervalMillis = JumpPieceDurationMillis

  val IllegalPieceSelectionDurationMillis = 750.0

  val PlacePiecesTopDelayMillis: Double = 250.0

  val PlacePiecesBottomDelayMillis: Double = 0.0

  val PlacePiecesIntervalMillis: Double = 175.0

  val PlacePieceDurationMillis: Double = 275.0

  val BoardRotateDurationMillis: Double = 1200.0
}