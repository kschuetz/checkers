package checkers.core

trait DrawLogic {
  def initialDrawStatus: DrawStatus

  def updateDrawStatus(input: DrawStatus,
                       turnIndex: Int,
                       board: BoardStateRead,
                       events: Int): DrawStatus
}