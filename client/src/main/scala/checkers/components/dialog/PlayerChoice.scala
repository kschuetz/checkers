package checkers.components.dialog

import checkers.core.Human

case class PlayerChoice(displayName: String,
                        programId: Option[String],
                        difficultyLevel: Int) {
  def isComputer = programId.nonEmpty
  def isHuman = programId.isEmpty
}

object PlayerChoice {
  val human = PlayerChoice(Human.displayName, Human.programId, Human.difficultyLevel)
}