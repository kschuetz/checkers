package checkers.core

import checkers.consts.Color


case class ComputerAction(move: List[Int],
                          proposeDraw: Boolean,
                          acceptDraw: Boolean)


case class MoveComputationResult[S](action: ComputerAction, state: S)

/**
  * MoveComputation is stateful and mutable.
  * @tparam S opaque state
  */
trait MoveComputation[S] {
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
    * Returns the result of the computation.  If not ready, throws an exception.
    * Call isReady first.
    * @return
    */
  def result: MoveComputationResult[S]

}


/**
  * ComputerPlayer must be immutable.
  * @tparam S opaque state
  */
trait ComputerPlayerState[S] {
  def state: S
  def color: Color
  def createMoveComputation(gameState: GameState): MoveComputation[S]
  def update(state: S): this.type
}


trait ComputerPlayer[S] extends Player {
  def create(color: Color, rulesSettings: RulesSettings): ComputerPlayerState[S]
}

trait Player {
  def isComputer: Boolean
  def isHuman: Boolean
}

case object Human extends Player {
  def isComputer = false
  def isHuman = true
}
