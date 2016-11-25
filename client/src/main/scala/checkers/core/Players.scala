package checkers.core

import checkers.computer.Program

trait HumanState
case object HumanState extends HumanState with Opaque

trait PlayerDescription {
  def displayName: String
  def isComputer: Boolean
  def isHuman: Boolean
  def programId: Option[String]
  def difficultyLevel: Int
}

sealed trait Player extends PlayerDescription {
  def initialState: Opaque
}
case object Human extends Player {
  val initialState = HumanState
  val displayName = "Human"
  val isComputer = false
  val isHuman = true
  val programId = None
  val difficultyLevel = 0
}
case class Computer(program: Program, displayName: String, programId: Option[String], difficultyLevel: Int) extends Player {
  def initialState: Opaque = program.initialState
  def isComputer = true
  def isHuman = false
}

