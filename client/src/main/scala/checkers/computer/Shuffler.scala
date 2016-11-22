package checkers.computer

trait Shuffler {
  def getMoveIndex(inputMoveIndex: Int, moveCount: Int, plyIndex: Int, pvMoveInFront: Boolean): Int
}

object NoShuffle extends Shuffler {
  def getMoveIndex(inputMoveIndex: Int, moveCount: Int, plyIndex: Int, pvMoveInFront: Boolean): Int = inputMoveIndex
}