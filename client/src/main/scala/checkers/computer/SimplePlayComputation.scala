package checkers.computer

import checkers.experiments._

/**
  * Provides a framework for very simple computations that will return
  * an answer almost immediately.
  */
abstract class SimplePlayComputation[S] extends PlayComputation[S] {
  private var answer: Option[(Play, S)] = None

  protected def compute: (Play, S)

  override def run(maxCycles: Int): Int = {
    if(isReady) 0
    else {
      answer = Some(compute)
      1
    }
  }

  override def result: (Play, S) = answer.get

  override def isReady: Boolean = answer.isDefined

  override def interrupt(): Unit = { }
}