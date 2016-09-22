package checkers.computer

import checkers.consts._
import checkers.core._
import checkers.test.BoardUtils.BoardStats
import checkers.test.generators.{BoardGenerators, ColorGenerator}
import checkers.test.{BoardUtils, DefaultGameLogicTestModule, TestSuiteBase}
import com.softwaremill.macwire._
import nyaya.gen._
import nyaya.prop._
import nyaya.test.PropTest._
import utest._
import utest.framework._

object EvaluatorTests extends TestSuiteBase
  with DefaultGameLogicTestModule
  with ColorGenerator
  with BoardGenerators {
  
  lazy val evaluator = wire[DefaultEvaluator]
  lazy val moveGenerator = gameLogicModule.moveGenerator
  lazy val moveDecoder = new MoveDecoder
  
  private def getMoveList(boardStack: BoardStack, turnToMove: Color): List[List[Int]] = {
    val moveList = moveGenerator.generateMoves(boardStack, turnToMove)
    moveDecoder.allPaths(moveList)
  }

  case class EvaluatorPropInput(board: BoardState,
                                turnToMove: Color,
                                testProbe: DefaultEvaluatorTestProbe,
                                darkMoves: List[List[Int]],
                                lightMoves: List[List[Int]],
                                boardStats: BoardStats,
                                evaluationResult: Int)
  
  lazy val genEvaluatorPropInput: Gen[EvaluatorPropInput] = for {
    turnToMove <- genColor
    board <- genBoard
  } yield {
    val boardStack = BoardStack.fromBoard(board)
    val darkMoves = getMoveList(boardStack, DARK)
    val lightMoves = getMoveList(boardStack, LIGHT)
    val boardStats = BoardUtils.getBoardStats(board)
    val testProbe = new DefaultEvaluatorTestProbe
    val evaluationResult = evaluator.evaluate(turnToMove, turnToMove, board, testProbe)
    EvaluatorPropInput(board, turnToMove, testProbe, darkMoves, lightMoves, boardStats, evaluationResult)    
  }

  private def testProbeCheck(name: String, f: (DefaultEvaluatorTestProbe, EvaluatorPropInput) => Boolean): Prop[EvaluatorPropInput] =
    Prop.test(name, { input => f(input.testProbe, input)} )

  lazy val darkManCount = testProbeCheck("darkManCount", _.darkMen == _.boardStats.darkMan)
  lazy val lightManCount = testProbeCheck("lightManCount", _.lightMen == _.boardStats.lightMan)
  lazy val darkKingCount = testProbeCheck("darkKingCount", _.darkKings == _.boardStats.darkKing)
  lazy val lightKingCount = testProbeCheck("lightKingCount", _.lightKings == _.boardStats.lightKing)

  lazy val evaluatorPropInputProps = darkManCount & lightManCount & darkKingCount & lightKingCount

  override def tests: Tree[Test] = TestSuite {
    'Evaluator {
      genEvaluatorPropInput.mustSatisfy(evaluatorPropInputProps)
    }

  }

}