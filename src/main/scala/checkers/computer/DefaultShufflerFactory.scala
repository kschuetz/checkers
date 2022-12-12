package checkers.computer

import checkers.util.{Random, Shuffle}

import scala.scalajs.js.typedarray.Int8Array

object DefaultShufflerFactory {
  val Levels = 24
}

class DefaultShufflerFactory extends ShufflerFactory {

  private def init(levels: Int): Int8Array = {
    val size = levels * (levels + 1) / 2
    val result = new Int8Array(size)
    var level = 0
    var i = 0
    while(level < levels) {
      var j = 0
      while(j <= level) {
        result(i) = j.toByte
        j += 1
        i += 1
      }
      level += 1
    }
    result
  }

  private def shuffle(data: Int8Array, levels: Int, stateIn: Random): Random = {
    var state = stateIn
    var level = 0
    var a = 0
    while(level < levels) {
      val b = a + level + 1
      state = Shuffle.shuffleSlice(data, a, b, state)
      a = b
      level += 1
    }
    state
  }

  def createShuffler(stateIn: ComputerPlayerState): (Shuffler, ComputerPlayerState) = {
    val levels = DefaultShufflerFactory.Levels
    val data = init(levels)
    val stateOut = {
      val r = shuffle(data, levels, stateIn.value)
      ComputerPlayerState(r)
    }
    val shuffler = new DefaultShuffler(data, levels)
    (shuffler, stateOut)
  }
}

class DefaultShuffler(val data: Int8Array, val levels: Int) extends Shuffler {
  override def getMoveIndex(inputMoveIndex: Int, moveCount: Int, plyIndex: Int, pvMoveInFront: Boolean): Int = {
    if(moveCount < 2) inputMoveIndex
    else if(pvMoveInFront) {
      if(inputMoveIndex == 0) 0 else 1 + getMoveIndexImpl(inputMoveIndex - 1, moveCount - 1)
    } else getMoveIndexImpl(inputMoveIndex, moveCount)
  }

  private def getMoveIndexImpl(inputMoveIndex: Int, moveCount: Int): Int = {
    val level = math.min(moveCount, levels) - 1
    if(inputMoveIndex >= level) inputMoveIndex
    else {
      val base = level * (level + 1) / 2
      data(base + inputMoveIndex).toInt
    }
  }
}