package checkers.computer

import checkers.core._
import checkers.util.Random


case class ComputerPlayerState(value: Random) extends Opaque {
  def nextInt(n: Int): (Int, ComputerPlayerState) = {
    val (result, nextRandom) = value.nextInt(n)
    (result, ComputerPlayerState(nextRandom))
  }
}


object ComputerPlayerState {
  def createRandom: ComputerPlayerState = {
    val value = Random()
    ComputerPlayerState(value)
  }

  def fromSeed(seed: Long): ComputerPlayerState = {
    val value = Random(seed)
    ComputerPlayerState(value)
  }
}