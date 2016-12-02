package checkers.userinterface.dialog

import checkers.core.Human

case class PlayerChoice(displayName: String,
                        programId: Option[String],
                        difficultyLevel: Int) {
  def isComputer: Boolean = programId.nonEmpty
  def isHuman: Boolean = programId.isEmpty
}

object PlayerChoice {
  val human = PlayerChoice(Human.displayName, Human.programId, Human.difficultyLevel)
}