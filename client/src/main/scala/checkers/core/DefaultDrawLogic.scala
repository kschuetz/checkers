package checkers.core

import checkers.consts._


case class ConcreteDrawStatus(turnOfLastAdvance: Int,
                              isDraw: Boolean,
                              turnsRemainingHintValue: Int,
                              positionTracker: PositionTracker) extends DrawStatus {
  def turnsRemainingHint: Option[Int] = if(turnsRemainingHintValue > 0) Some(turnsRemainingHintValue) else None
}

class DefaultDrawLogic(rulesSettings: RulesSettings) extends DrawLogic {

  protected val TurnLimit = 100   // number of moves with no advance or capture
  protected val TurnsRemainingWarningThreshold = 20

  protected val PositionRepeatCountForDraw = 3   // number of times an exact position occurring results in a draw

  def initialDrawStatus: DrawStatus = ConcreteDrawStatus(0, isDraw = false, 0, PositionTracker.empty)

  def updateDrawStatus(input: DrawStatus, turnIndex: Int, board: BoardStateRead, events: Int): DrawStatus = {
    val status = input.asInstanceOf[ConcreteDrawStatus]
    val wasAdvance = (events & (PIECECAPTURED | PIECEADVANCED | PIECECROWNED)) != 0
    if(wasAdvance) {
      // If there was an advance, it is guaranteed that all previous positions will never occur again,
      // since either a capture occurred (resulting in fewer pieces), or a normal piece moved (and that
      // piece can never be moved back until it is a king).
      //
      // Therefore we can reset the position tracker.

      ConcreteDrawStatus(turnIndex, isDraw = false, 0, PositionTracker.reset(board))
    } else {
      val (positionRepeatCount, nextPositionTracker) = status.positionTracker.addPosition(board)

      if(positionRepeatCount >= PositionRepeatCountForDraw) {
        // draw that was caused by a position repeat

        status.copy(isDraw = true, turnsRemainingHintValue = 0, positionTracker = nextPositionTracker)
      } else {
        val turnsSinceAdvance = turnIndex - status.turnOfLastAdvance
        val turnsRemaining = math.max(0, TurnLimit - turnsSinceAdvance)
        val hintValue = if(turnsRemaining <= TurnsRemainingWarningThreshold) turnsRemaining else 0
        val isDraw = turnsRemaining <= 0
        status.copy(isDraw = isDraw, turnsRemainingHintValue = hintValue)
      }

    }
  }
}