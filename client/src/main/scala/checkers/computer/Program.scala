package checkers.computer

import checkers.consts._
import checkers.core._

case class PlayInput(boardState: BoardState,
                     rulesSettings: RulesSettings,
                     turnToMove: Color,
                     drawStatus: DrawStatus,
                     history: List[HistoryEntry])

trait Program[S] {
  def initialState: S

  def play(state: S, playInput: PlayInput): PlayComputation[S]
}