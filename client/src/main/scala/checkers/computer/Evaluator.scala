package checkers.computer

import checkers.consts.Color
import checkers.core.BoardStateRead

trait Evaluator {
  def evaluate(turnToPlay: Color, board: BoardStateRead, testProbe: AnyRef = null): Int
}