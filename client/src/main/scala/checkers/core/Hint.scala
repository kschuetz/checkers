package checkers.core

import checkers.computer.{PlayComputation, Program}
import checkers.consts.Side

case class Hint(startSquare: Int, endSquare: Int)


sealed trait HintState {
  def hintButtonVisible: Boolean = false
  def waitingForComputer: Boolean = false
}

case object NoMentorAvailable extends HintState

case class MentorAvailable(mentor: Program, mentorOpaque: Opaque) extends HintState {
  override val hintButtonVisible = true
}
case class ComputingHint(startTime: Double, side: Side, playComputation: PlayComputation) extends HintState {
  override def hintButtonVisible = true
  override def waitingForComputer = true
}
case class HintAvailable(hint: Hint) extends HintState {
  override def hintButtonVisible = true
}