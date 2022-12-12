package checkers.benchmarks.suites

import checkers.consts._
import checkers.core._
import checkers.computer._
import checkers.util.{NullPerformanceClock, PerformanceClock}
import com.softwaremill.macwire._
import japgolly.scalajs.benchmark.gui._
import japgolly.scalajs.benchmark.{Benchmark, _}

object Searcher {

  private case class Params(searcher: Searcher,
                            playInput: PlayInput,
                            incomingPlayerState: ComputerPlayerState,
                            shuffler: Shuffler)


  private def createParams(drawLogicEnabled: Boolean, shufflerEnabled: Boolean): Params = {
    val gameLogicModule = new DefaultGameLogicModule {
      override lazy val drawLogic: DrawLogic = if (drawLogicEnabled) {
        wire[DefaultDrawLogic]
      } else NullDrawLogic

      override lazy val shufflerFactory: ShufflerFactory = if (shufflerEnabled) {
        wire[DefaultShufflerFactory]
      } else NoShuffleFactory

      def performanceClock: PerformanceClock = NullPerformanceClock
    }

    val playerState0 = ComputerPlayerState.createRandom
    val (shuffler, playerState1) = gameLogicModule.shufflerFactory.createShuffler(playerState0)
    val drawStatus = gameLogicModule.drawLogic.initialDrawStatus
    val board = gameLogicModule.boardInitializer.initialBoard(gameLogicModule.rulesSettings)
    val playInput = PlayInput(board, 0, gameLogicModule.rulesSettings, DARK, drawStatus, Vector.empty)
    Params(gameLogicModule.searcher, playInput, playerState1, shuffler)
  }

  private lazy val testCase1 = createParams(drawLogicEnabled = true, shufflerEnabled = true)
  private lazy val testCase2 = createParams(drawLogicEnabled = true, shufflerEnabled = false)
  private lazy val testCase3 = createParams(drawLogicEnabled = false, shufflerEnabled = true)
  private lazy val testCase4 = createParams(drawLogicEnabled = false, shufflerEnabled = false)


  private def runSearch(params: Params): Unit = {
    val search = params.searcher.create(params.playInput, params.incomingPlayerState,
      None, None, params.shuffler, identity)

    search.run(1000)
  }

  val suite = GuiSuite(
    Suite("Searcher Benchmarks")(

      Benchmark("DefaultDrawLogic, DefaultShuffler") {
        runSearch(testCase1)
      },

      Benchmark("DefaultDrawLogic, no shuffler") {
        runSearch(testCase2)
      },

      Benchmark("no draw logic, DefaultShuffler") {
        runSearch(testCase3)
      },

      Benchmark("no draw logic, no shuffler") {
        runSearch(testCase4)
      }

    )
  )


}
