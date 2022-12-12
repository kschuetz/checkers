package checkers.util

import org.scalajs.dom.window.performance

trait PerformanceClock {
  def now(): Double
}

object DomPerformanceClock extends PerformanceClock {
  def now(): Double = performance.now()
}

object NullPerformanceClock extends PerformanceClock {
  def now(): Double = 0
}
