package checkers.benchmarks.suites

import checkers.consts._
import checkers.computer.DefaultEvaluator
import checkers.core.{Board, BoardState, DefaultBoardInitializer, RulesSettings}
import checkers.test.BoardUtils
import japgolly.scalajs.benchmark._
import gui._

object Evaluator {

  private val rulesSettings = RulesSettings.default

  private val evaluator = new DefaultEvaluator(rulesSettings)
  private val emptyBoard = BoardState.empty

  private val startingBoard = DefaultBoardInitializer.initialBoard(rulesSettings)

  private val sampleBoard1 = BoardUtils.parseBoard(
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