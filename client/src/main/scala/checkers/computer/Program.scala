package checkers.computer

import checkers.consts._
import checkers.core.{BoardState, DrawStatus, Play, RulesSettings}

case class PlayInput(boardState: BoardState,
                     rulesSettings: RulesSettings,
                     turnToMove: Color,
                     drawStatus: DrawStatus,
                     playHistory: List[Play],
                     boardHistory: List[BoardState])

trait Program[S] {
  def initialState: S

  def play(state: S, playInput: PlayInput): PlayComputation[S]
}