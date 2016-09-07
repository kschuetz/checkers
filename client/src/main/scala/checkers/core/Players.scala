package checkers.core

import checkers.computer.Program

trait HumanState
case object HumanState extends HumanState with Opaque

trait PlayerDescription {
  def displayName: String
  def isComputer: Boolean
  def isHuman: Boolean
}

sealed trait Player extends PlayerDescription {
  def initialState: Opaque
}
case object Human extends Player {
  val initialState = HumanState
  val displayName = "Human"
  val isComputer = false
  val isHuman = true
}
case class Computer(program: Program) extends Player {
  def initialState = program.initialState
  val displayName = "Computer"// TODO
  def isComputer = true
  def isHuman = false
}

