package checkers.computer

import checkers.computer.TrivialPlayer._
import checkers.core._
import checkers.util.Random


case class ComputerPlayerState(value: Random) extends Opaque {
  def nextInt(n: Int): (Int, State) = {
    val (result, nextRandom) = value.nextInt(n)
    (result, State(nextRandom))
  }
}
