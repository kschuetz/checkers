package checkers.core

case class PlayEvents(remainingMoveTree: MoveTree,
                      drawProposed: Boolean,
                      drawAccepted: Boolean) {
  def endedTurn = remainingMoveTree.isEmpty
}


object PlayEvents {
  val empty = PlayEvents(MoveTree.empty, drawProposed = false, drawAccepted = false)
  val turnEnded = empty
  def partialTurn(remainingMoveTree: MoveTree) = empty.copy(remainingMoveTree = remainingMoveTree)
  val acceptedDraw = turnEnded.copy(drawAccepted = true)
}