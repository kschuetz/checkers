package checkers.computer

trait ShufflerFactory {
  def createShuffler(stateIn: ComputerPlayerState): (Shuffler, ComputerPlayerState)
}

object NoShuffleFactory extends ShufflerFactory {
  def createShuffler(stateIn: ComputerPlayerState): (Shuffler, ComputerPlayerState) = (NoShuffle, stateIn)
}