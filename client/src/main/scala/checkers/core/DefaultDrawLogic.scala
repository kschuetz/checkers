package checkers.core

import checkers.consts._


case class ConcreteDrawStatus(turnOfLastAdvance: Int,
                              isDraw: Boolean,
                              turnsRemainingHintValue: Int) extends DrawStatus {
  def turnsRemainingHint: Option[Int] = if(turnsRemainingHintValue > 0) Some(turnsRemainingHintValue) else None
}

class DefaultDrawLogic(rulesSettings: RulesSettings) extends DrawLogic {

  protected val TurnLimit = 100   // number of moves with no advance or capture
  protected val TurnsRemainingWarningThreshold = 20

  def initialDrawStatus: DrawStatus = ConcreteDrawStatus(0, isDraw = false, 0)

  def updateDrawStatus(input: DrawStatus, turnIndex: Int, board: BoardStateRead, events: Int): DrawStatus = {
    val status = input.asInstanceOf[ConcreteDrawStatus]
    val wasAdvance = (events & (PIECECAPTURED | PIECEADVANCED | PIECECROWNED)) != 0
    if(wasAdvance) {
      ConcreteDrawStatus(turnIndex, isDraw = false, 0)
    } else {
      val turnsSinceAdvance = turnIndex - status.turnOfLastAdvance
      val turnsRemaining = math.max(0, TurnLimit - turnsSinceAdvance)
      val hintValue = if(turnsRemaining <= TurnsRemainingWarningThreshold) turnsRemaining else 0
      val isDraw = turnsRemaining <= 0
      status.copy(isDraw = isDraw, turnsRemainingHintValue = hintValue)
    }
  }
}