package checkers.computer

import checkers.consts._
import checkers.core.BoardState

case class PlayInput(boardState: BoardState, color: Color)

trait Program[S] {
  def initialState: S

  def play(state: S, playInput: PlayInput): PlayComputation[S]
}