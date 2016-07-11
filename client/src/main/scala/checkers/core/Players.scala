package checkers.core

import checkers.computer.Program

trait HumanState
case object HumanState extends HumanState

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

