package checkers.computer

import checkers.experiments._

trait PlayComputation[S] {
  def run(maxCycles: Int): Int

  def interrupt(): Unit

  def isReady: Boolean

  def result: (Play, S)
}