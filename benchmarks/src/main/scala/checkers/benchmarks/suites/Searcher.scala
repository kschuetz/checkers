package checkers.benchmarks.suites

import checkers.consts._
import checkers.computer.{ComputerPlayerState, DefaultEvaluator, DefaultShufflerFactory}
import checkers.core._
import checkers.test.BoardUtils
import japgolly.scalajs.benchmark._
import gui._

object Searcher {

  private val rulesSettings = RulesSettings.default

//  private val moveExecutor = new MoveExecutor()

  private val evaluator = new DefaultEvaluator(rulesSettings)
  private val emptyBoard = BoardState.empty

  private val startingBoard = DefaultBoardInitializer.initialBoard(rulesSettings)

  private val shufflerFactory = new DefaultShufflerFactory

  private val shuffler = shufflerFactory.createShuffler(ComputerPlayerState.createRandom)

  val suite = GuiSuite(
    Suite("Searcher Benchmarks")(

      Benchmark("with DefaultDrawLogic and DefaultShuffler") {

      }

    )
  )


}