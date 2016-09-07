package checkers.computer

import checkers.core.{Opaque, Play}

/**
  * MoveComputation is stateful and mutable.
  */
trait PlayComputation {
  /**
    * Gives the computation a slice of time to execute.  Updates internal state, and affects isReady status.
    *
    * @param maxCycles The maximum number of cycles to execute in this frame.  Higher numbers
    *                  will yield a result faster, but will potentially make the UI less responsive.
    * @return the number of cycles actually used (0..maxCycles) in this call.
    */
  def run(maxCycles: Int): Int

  /**
    * Advises the computation to wrap things up very soon.  Does not necessarily automatically yield a result,
    * so be sure to keep calling run until a result is ready.
    */
  def interrupt(): Unit


  /**
    * If true, a call to result will yield a result.
    */
  def isReady: Boolean

  /**
    * Returns the result of the computation, and the new player state.  If not ready, throws an exception.
    * Call isReady first.
    * @return
    */
  def result: (Play, Opaque)
}