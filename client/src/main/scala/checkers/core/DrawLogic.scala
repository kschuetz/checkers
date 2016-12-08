package checkers.core

import checkers.consts.Side

trait DrawLogic {
  def initialDrawStatus: DrawStatus

  def updateDrawStatus(input: DrawStatus,
                       turnIndex: Int,
                       board: BoardStateRead,
                       events: Int): DrawStatus
}