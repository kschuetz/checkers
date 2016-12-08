package checkers.core

class DefaultDrawLogic(rulesSettings: RulesSettings) extends DrawLogic {
  def initialDrawStatus: DrawStatus = NullDrawStatus

  def updateDrawStatus(input: DrawStatus, turnIndex: Int, board: BoardStateRead, events: Int): DrawStatus = input
}