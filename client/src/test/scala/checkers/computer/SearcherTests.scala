package checkers.computer

import checkers.consts._
import checkers.core._
import checkers.logger
import checkers.logger.NullLogger
import checkers.test.generators.{BoardGenerators, SideGenerator}
import checkers.test.{BoardUtils, DefaultGameLogicTestModule, TestSuiteBase}
import nyaya.gen._
import nyaya.prop._
import nyaya.test.PropTest._
import utest._
import utest.framework._

object SearcherTests extends TestSuiteBase
  with DefaultGameLogicTestModule
  with SideGenerator
  with BoardGenerators {


  private lazy val searcher = gameLogicModule.searcher.withLogger(NullLogger)

  private lazy val drawLogic = gameLogicModule.drawLogic

  private lazy val initialComputerState = ComputerPlayerState.fromSeed(20161212)


  case class PropInput(board: BoardState,
                       turnToMove: Side,
                       maxCycles: Int)

  case class SameMoveChoiceTestCase(turnToMove: Int,
                                    maxCycles: Int,
                                    board1: BoardState,
                                    board2: BoardState,
                                    play1: Play,
                                    play2: Play)

  private lazy val genPropInput: Gen[PropInput] = for {
    turnToMove <- genSide
    board <- genBoard
    maxCycles <- Gen.choose(500, 1000, 2000)
  } yield PropInput(board, turnToMove, maxCycles)

  private lazy val genSameMoveChoiceTestCase: Gen[SameMoveChoiceTestCase] = genPropInput.map { input =>
    val shuffler = NoShuffle
    val play1 = getBestMove(input.maxCycles, input.board, input.turnToMove, shuffler)

    val flippedBoard = BoardUtils.swapSides(input.board)
    val play2 = getBestMove(input.maxCycles, flippedBoard, OPPONENT(input.turnToMove), shuffler)

    SameMoveChoiceTestCase(input.turnToMove, input.maxCycles, input.board, flippedBoard, play1, play2)
  }

  private def getBestMove(maxCycles: Int, board: BoardState, turnToMove: Side, shuffler: Shuffler): Play = {
    val playInput = PlayInput(board, 0, rulesSettings, turnToMove, drawLogic.initialDrawStatus, Nil)
    val search = searcher.create(playInput, initialComputerState, None, Some(maxCycles), shuffler, identity)
    while (!search.isReady) {
      search.run(maxCycles)
      search.rush()
    }
    search.result.play
  }

  private lazy val sameMoveChoiceBothSides: Prop[SameMoveChoiceTestCase] = Prop.test("sameMoveChoiceBothSides", { input =>
    val flippedPlay2 = Play.swapSides(input.play2)

    input.play1 == flippedPlay2
  })

  override def tests: Tree[Test] = TestSuite {
    'Searcher {
      genSameMoveChoiceTestCase.mustSatisfy(sameMoveChoiceBothSides)
    }

  }
}
