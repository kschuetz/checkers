package checkers.computer

import checkers.consts.Color
import checkers.core.BoardStateRead

trait Evaluator {
  def evaluate(color: Color, turnToPlay: Color, board: BoardStateRead): Int
}