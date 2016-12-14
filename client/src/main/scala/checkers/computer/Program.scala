package checkers.computer

import checkers.consts._
import checkers.core._

case class PlayInput(board: BoardState,
                     turnIndex: Int,
                     rulesSettings: RulesSettings,
                     turnToMove: Side,
                     drawStatus: DrawStatus,
                     history: Vector[HistoryEntry])

trait Program {
  def initialOpaque: Opaque

  def play(state: Opaque, playInput: PlayInput): PlayComputation
}