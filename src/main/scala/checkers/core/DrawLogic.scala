package checkers.core

import checkers.consts.Side

trait DrawLogic {
  def initialDrawStatus: DrawStatus

  def updateDrawStatus(input: DrawStatus,
                       turnIndex: Int,
                       board: BoardStateRead,
                       events: Int): DrawStatus
}


object NullDrawLogic extends DrawLogic {
  override def initialDrawStatus: DrawStatus = NullDrawStatus

  override def updateDrawStatus(input: DrawStatus, turnIndex: Side, board: BoardStateRead, events: Side): DrawStatus = input
}