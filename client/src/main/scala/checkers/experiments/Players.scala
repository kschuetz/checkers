package checkers.experiments

import checkers.computer.Program

trait HumanState
case object HumanState extends HumanState

case class Play(move: List[Int], proposeDraw: Boolean, acceptDraw: Boolean)

object Play {
  val empty = Play(Nil, proposeDraw=false, acceptDraw=false)

  def move(path: List[Int]) = Play(path, proposeDraw=false, acceptDraw=false)
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

