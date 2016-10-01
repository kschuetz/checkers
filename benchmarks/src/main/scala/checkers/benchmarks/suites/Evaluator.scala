package checkers.benchmarks.suites

import checkers.consts._
import checkers.computer.DefaultEvaluator
import checkers.core.{Board, BoardState, RulesSettings}
import checkers.test.BoardUtils
import japgolly.scalajs.benchmark._
import gui._

object Evaluator {

  val evaluator = new DefaultEvaluator(RulesSettings.default)
  val emptyBoard = BoardState.empty

  val startingBoard = RulesSettings.initialBoard(RulesSettings.default)

  val sampleBoard1 = BoardUtils.parseBoard(
    """
        - - - -
       - - - -
        d l L -
       - l - -
        - l L -
       D d d d
        l - - -
       - - - -
    """)

  val suite = GuiSuite(
    Suite("Evaluator Benchmarks")(

      Benchmark("empty board x 100") {
        var i = 0
        while (i < 50) {
          evaluator.evaluate(DARK, emptyBoard)
          evaluator.evaluate(LIGHT, emptyBoard)
          i += 1
        }
      },

      Benchmark("starting board x 100") {
        var i = 0
        while (i < 50) {
          evaluator.evaluate(DARK, startingBoard)
          evaluator.evaluate(LIGHT, startingBoard)
          i += 1
        }
      },

      Benchmark("sample board1 x 100") {
        var i = 0
        while (i < 50) {
          evaluator.evaluate(DARK, sampleBoard1)
          evaluator.evaluate(LIGHT, sampleBoard1)
          i += 1
        }
      }

    )
  )


}