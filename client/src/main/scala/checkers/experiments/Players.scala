package checkers.experiments

import checkers.core.BoardState
import checkers.consts.Color

trait HumanState
case object HumanState extends HumanState

case class PlayInput(boardState: BoardState, color: Color)

case class Play(move: List[Int], proposeDraw: Boolean, acceptDraw: Boolean)

object Play {
  val empty = Play(Nil, proposeDraw=false, acceptDraw=false)

  def move(path: List[Int]) = Play(path, proposeDraw=false, acceptDraw=false)
}

trait PlayComputation[S] {
  def run(maxCycles: Int): Int

  def interrupt(): Unit

  def isReady: Boolean

  def result: (Play, S)
}

trait Program[S] {
  def initialState: S

  def play(state: S, playInput: PlayInput): PlayComputation[S]
}

sealed trait Player[S] {
  type State = S
  def initialState: S
}
case object Human extends Player[HumanState] {
  val initialState = HumanState
}
case class Computer[S](program: Program[S]) extends Player[S] {
  def initialState = program.initialState
}

