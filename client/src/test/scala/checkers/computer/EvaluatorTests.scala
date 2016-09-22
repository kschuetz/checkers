package checkers.computer

import checkers.consts._
import checkers.core.MoveExecutorTests._
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
  lazy val jumpTable = tablesModule.jumpTable

  private def getMoveList(boardStack: BoardStack, turnToMove: Color): List[List[Int]] = {
    val moveList = moveGenerator.generateMoves(boardStack, turnToMove)
    moveDecoder.allPaths(moveList)
  }

  private def moveToAttack(path: List[Int]): Int = path match {
    // only consider first move in path;  we only care about immediate attacks
    case from :: to :: _ => jumpTable.getMiddle(from, to)
    case _ => -1
  }

  private def movesToAttacks(paths: List[List[Int]]) = paths.map(moveToAttack).filter(_ >= 0).toSet

  case class ProbeData(darkMen: Int,
                       darkKings: Int,
                       lightMen: Int,
                       lightKings: Int,
                       potentialAttacks: Int,
                       darkAttacks: Int,
                       lightAttacks: Int,
                       safeForDark: Int,
                       safeForLight: Int,
                       darkAttackSet: Set[Int],
                       lightAttackSet: Set[Int])

  object ProbeData {
    def fromTestProbe(input: DefaultEvaluatorTestProbe): ProbeData = {
      ProbeData(input.darkMen,
        input.darkKings,
        input.lightMen,
        input.lightKings,
        input.potentialAttacks,
        input.darkAttacks,
        input.lightAttacks,
        input.safeForDark,
        input.safeForLight,
        darkAttackSet = BoardUtils.squareMaskToSet(input.darkAttacks),
        lightAttackSet = BoardUtils.squareMaskToSet(input.lightAttacks))
    }
  }

  case class EvaluatorPropInput(board: BoardState,
                                turnToMove: Color,
                                probeData: ProbeData,
                                darkMoves: List[List[Int]],
                                lightMoves: List[List[Int]],
                                darkAttacks: Set[Int],
                                lightAttacks: Set[Int],
                                boardStats: BoardStats,
                                evaluationResult: Int)

  lazy val genEvaluatorPropInput: Gen[EvaluatorPropInput] = for {
    turnToMove <- genColor
    board <- genBoard
  } yield {
    val boardStack = BoardStack.fromBoard(board)
    val darkMoves = getMoveList(boardStack, DARK)
    val lightMoves = getMoveList(boardStack, LIGHT)
    val darkAttacks = movesToAttacks(darkMoves)
    val lightAttacks = movesToAttacks(lightMoves)
    val boardStats = BoardUtils.getBoardStats(board)
    val testProbe = new DefaultEvaluatorTestProbe
    val evaluationResult = evaluator.evaluate(turnToMove, turnToMove, board, testProbe)
    val probeData = ProbeData.fromTestProbe(testProbe)
    EvaluatorPropInput(board, turnToMove, probeData, darkMoves, lightMoves, darkAttacks, lightAttacks, boardStats, evaluationResult)
  }

  private def testProbeCheck(name: String, f: (ProbeData, EvaluatorPropInput) => Boolean): Prop[EvaluatorPropInput] =
    Prop.test(name, { input => f(input.probeData, input) })

  lazy val darkManCount = testProbeCheck("darkManCount", _.darkMen == _.boardStats.darkMan)
  lazy val lightManCount = testProbeCheck("lightManCount", _.lightMen == _.boardStats.lightMan)
  lazy val darkKingCount = testProbeCheck("darkKingCount", _.darkKings == _.boardStats.darkKing)
  lazy val lightKingCount = testProbeCheck("lightKingCount", _.lightKings == _.boardStats.lightKing)

  lazy val darkAttacks = testProbeCheck("darkAttacks", _.darkAttackSet == _.darkAttacks)

  lazy val evaluatorPropInputProps = darkManCount & lightManCount & darkKingCount & lightKingCount &
    darkAttacks


  override def tests: Tree[Test] = TestSuite {
    'Evaluator {
      genEvaluatorPropInput.mustSatisfy(evaluatorPropInputProps)
    }

  }

}