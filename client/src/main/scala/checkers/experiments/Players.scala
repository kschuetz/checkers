package checkers.experiments

import checkers.core.BoardState

trait HumanState
case object HumanState extends HumanState

case class PlayInput(boardState: BoardState)

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

