package checkers.computer

import checkers.consts._
import checkers.core._

case class PlayInput(board: BoardState,
                     rulesSettings: RulesSettings,
                     turnToMove: Color,
                     drawStatus: DrawStatus,
                     history: List[HistoryEntry])

trait Program {
  def initialState: Opaque

  def play(state: Opaque, playInput: PlayInput): PlayComputation
}