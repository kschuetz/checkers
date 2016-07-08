package checkers.experiments

class TrivialPlayerState

class TrivialPlayer(initialSeed: Long) extends Program[TrivialPlayerState] {
  override def initialize = new TrivialPlayerState

  override def play(state: TrivialPlayerState, playInput: PlayInput): PlayComputation[TrivialPlayerState] = ???
}