package checkers.computer

/**
  * Provides a framework for very simple computations that will return
  * an answer almost immediately.
  */
abstract class SimplePlayComputation extends PlayComputation {
  private var tickCount: Int = 0
  private var answer: Option[PlayResult] = None

  protected def compute: PlayResult

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

  override def result: PlayResult = answer.get

  override def isReady: Boolean = answer.isDefined

  override def rush(): Unit = { }
}