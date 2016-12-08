package checkers.core

class DefaultDrawLogic(rulesSettings: RulesSettings) extends DrawLogic {
  def initialDrawStatus: DrawStatus = NullDrawStatus

  def updateDrawStatus(input: DrawStatus, turnIndex: Int, board: BoardStateRead, events: Int): DrawStatus = {
    input
//    println(s"updateDrawStatus: $turnIndex")
//    if(turnIndex < 3) input
//    else new DrawStatus {
//      override def isDraw: Boolean = true
//
//      override def turnsRemainingHint: Option[Int] = None
//    }
  }
}