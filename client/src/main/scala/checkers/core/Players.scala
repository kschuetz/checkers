package checkers.core

import checkers.computer.Program

trait HumanState
case object HumanState extends HumanState

trait PlayerDescription {
  def displayName: String
}

sealed trait Player[S] extends PlayerDescription {
  type State = S
  def initialState: S
}
case object Human extends Player[HumanState] {
  val initialState = HumanState
  val displayName = "Human"
}
case class Computer[S](program: Program[S]) extends Player[S] {
  def initialState = program.initialState
  val displayName = "Computer"// TODO
}

