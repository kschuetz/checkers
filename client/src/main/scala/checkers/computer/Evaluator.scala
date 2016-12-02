package checkers.computer

import checkers.consts.Side
import checkers.core.BoardStateRead

trait Evaluator {
  def evaluate(turnToPlay: Side, board: BoardStateRead, testProbe: AnyRef = null): Int
}