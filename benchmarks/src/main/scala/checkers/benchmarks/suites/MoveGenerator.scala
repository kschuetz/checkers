package checkers.benchmarks.suites

import checkers.consts._
import checkers.core._
import checkers.core.tables.{JumpTable, NeighborTable}
import checkers.test.BoardUtils
import japgolly.scalajs.benchmark.gui._
import japgolly.scalajs.benchmark.{Benchmark, _}

object MoveGenerator {
  import com.softwaremill.macwire._

  lazy val rulesSettings = RulesSettings.default

  lazy val neighborTable: NeighborTable = wire[NeighborTable]

  lazy val jumpTable: JumpTable = wire[JumpTable]

  lazy val moveExecutor: MoveExecutor = wire[MoveExecutor]

  lazy val moveGenerator: MoveGenerator = wire[DefaultMoveGenerator]

  val startingBoard = DefaultBoardInitializer.initialBoard(rulesSettings)
  val startingBoardStack = BoardStack.fromBoard(startingBoard)

  val compoundJumpsBoard1 = BoardUtils.parseBoard(
    """
        l l - -
       - - - -
        - l l -
       - l - -
        - l L -
       D d d d
        l - - -
       - - - -
    """)
  val compoundJumpsBoard1Stack = BoardStack.fromBoard(compoundJumpsBoard1)

  val compoundJumpsBoard2 = BoardUtils.parseBoard(
    """
      - - - -
     - l l l
      - - d -
     - - - -
      d d d -
     - - - L
      - d d -
     - - - -
    """)
  val compoundJumpsBoard2Stack = BoardStack.fromBoard(compoundJumpsBoard2)


  val suite = GuiSuite(
    Suite("MoveGenerator Benchmarks")(

      Benchmark("starting board dark") {
        moveGenerator.generateMoves(startingBoardStack, DARK)
      },

      Benchmark("starting board light") {
        moveGenerator.generateMoves(startingBoardStack, LIGHT)
      },

      Benchmark("compound jumps 1 dark") {
        moveGenerator.generateMoves(compoundJumpsBoard1Stack, DARK)
      },

      Benchmark("compound jumps 1 light") {
        moveGenerator.generateMoves(compoundJumpsBoard1Stack, LIGHT)
      },

      Benchmark("compound jumps 2 dark") {
        moveGenerator.generateMoves(compoundJumpsBoard1Stack, DARK)
      },

      Benchmark("compound jumps 2 light") {
        moveGenerator.generateMoves(compoundJumpsBoard1Stack, LIGHT)
      }
    )
  )


}