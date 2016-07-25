package checkers.core

case class PlayEvents(endedTurn: Boolean,
                      drawProposed: Boolean,
                      drawAccepted: Boolean)


object PlayEvents {
  val empty = PlayEvents(endedTurn = false, drawProposed = false, drawAccepted = false)
  val turnEnded = empty.copy(endedTurn = true)
  val partialTurn = empty
  val acceptedDraw = turnEnded.copy(drawAccepted = true)
}