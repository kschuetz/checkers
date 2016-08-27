package checkers.computer

import checkers.core.Play

/**
  * Provides a framework for very simple computations that will return
  * an answer almost immediately.
  */
abstract class SimplePlayComputation[S] extends PlayComputation[S] {
  private var tickCount: Int = 0
  private var answer: Option[(Play, S)] = None

  protected def compute: (Play, S)

  override def run(maxCycles: Int): Int = {
    if(isReady) 0
    else {
      tickCount += 1

      // Wait a couple of ticks before delivering
      if(tickCount >= 10) {
        answer = Some(compute)
      }
      1
    }
  }

  override def result: (Play, S) = answer.get

  override def isReady: Boolean = answer.isDefined

  override def interrupt(): Unit = { }
}