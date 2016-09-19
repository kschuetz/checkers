package checkers.computer

import checkers.core.{Opaque, Play}

class ImmediateResult(play: Play, stateOut: Opaque) extends PlayComputation {

  override def run(maxCycles: Int): Int = 0

  override def interrupt(): Unit = { }

  override def isReady: Boolean = true

  override def result: (Play, Opaque) = (play, stateOut)
}