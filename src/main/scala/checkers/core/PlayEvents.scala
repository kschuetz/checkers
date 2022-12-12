package checkers.core

case class PlayEvents(remainingMoveTree: MoveTree,
                      drawProposed: Boolean,
                      drawAccepted: Boolean) {
  def endedTurn: Boolean = remainingMoveTree.isEmpty
}


object PlayEvents {
  val empty = PlayEvents(MoveTree.empty, drawProposed = false, drawAccepted = false)
  val turnEnded: PlayEvents = empty
  def partialTurn(remainingMoveTree: MoveTree): PlayEvents = empty.copy(remainingMoveTree = remainingMoveTree)
  val acceptedDraw: PlayEvents = turnEnded.copy(drawAccepted = true)
}