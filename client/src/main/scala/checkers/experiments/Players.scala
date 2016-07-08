package checkers.experiments

import checkers.core.BoardState
import checkers.consts.Color

trait HumanState
case object HumanState extends HumanState

case class PlayInput(boardState: BoardState, color: Color)

case class Play(move: List[Int])

trait PlayComputation[S] {
  def run(maxCycles: Int): Int

  def interrupt(): Unit

  def isReady: Boolean

  def result: (Play, S)
}

trait Program[S] {
  def initialize: S

  def play(state: S, playInput: PlayInput): PlayComputation[S]
}

sealed trait Player[S] {
  def initialize: S
}
case object Human extends Player[HumanState] {
  val initialize = HumanState
}
case class Computer[S](program: Program[S]) extends Player[S] {
  def initialize = program.initialize
}

case class PlayersState[DS, LS](dark: DS, light: LS)


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