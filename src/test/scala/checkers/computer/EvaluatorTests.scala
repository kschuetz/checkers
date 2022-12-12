package checkers.computer

import checkers.consts._
import checkers.core.Board.BoardStats
import checkers.core._
import checkers.core.tables.NeighborIndex
import checkers.test.generators.{BoardGenerators, SideGenerator}
import checkers.test.{DefaultGameLogicTestModule, TestSuiteBase}
import com.softwaremill.macwire._
import nyaya.gen._
import nyaya.prop._
import nyaya.test.PropTest._
import utest._

object EvaluatorTests extends TestSuiteBase
  with DefaultGameLogicTestModule
  with SideGenerator
  with BoardGenerators {

  private lazy val evaluator = wire[DefaultEvaluator]
  private lazy val moveGenerator = gameLogicModule.moveGenerator
  private lazy val moveDecoder = new MoveDecoder
  private lazy val neighborTable = tablesModule.neighborTable
  private lazy val innerSquares: Set[Int] = Board.squareMaskToSet(checkers.masks.INNER)

  private def getMoveList(boardStack: BoardStack, turnToMove: Side): List[List[Int]] = {
    val moveList = moveGenerator.generateMoves(boardStack, turnToMove)
    moveDecoder.allPaths(moveList)
  }

  case class ExpectedSquares(dark: Set[Int], light: Set[Int])

  private def isSide(side: Side, occupant: Occupant): Boolean = ISPIECE(occupant) && SIDE(occupant) == side

  private def getExpectedAttacks(board: BoardStateRead): ExpectedSquares = {
    def evaluateForSide(side: Side, king: Occupant, neighbors: NeighborIndex): Set[Int] = {
      var result = Set.empty[Int]
      innerSquares.foreach { idx =>
        val isAttack = board.isSquareEmpty(idx) && {
          val forwardW = board.getOccupant(neighbors.forwardMoveW(idx))
          val forwardE = board.getOccupant(neighbors.forwardMoveE(idx))
          val backW = board.getOccupant(neighbors.backMoveW(idx))
          val backE = board.getOccupant(neighbors.backMoveE(idx))

          (isSide(side, backW) && !isSide(side, forwardE)) ||
            (isSide(side, backE) && !isSide(side, forwardW)) ||
            (!isSide(side, backW) && forwardE == king) ||
            (!isSide(side, backE) && forwardW == king)
        }
        if (isAttack) result += idx
      }
      result
    }

    ExpectedSquares(dark = evaluateForSide(DARK, DARKKING, neighborTable.Dark),
      light = evaluateForSide(LIGHT, LIGHTKING, neighborTable.Light))
  }

  private def getExpectedTrappedKings(board: BoardStateRead): ExpectedSquares = {
    def evaluateForSide(side: Side, king: Occupant, opponentKing: Occupant, neighbors: NeighborIndex): Set[Int] = {
      val opponent = OPPONENT(side)
      val opponentAt = board.squareHasSide(opponent) _
      var result = Set.empty[Int]

      def checkEscape(forward: Int, move: Int, jump: Int, side: Int): Boolean = {
        if (move < 0) return false
        if (jump >= 0 && opponentAt(move) && board.isSquareEmpty(jump)) return true
        if (!board.isSquareEmpty(move)) return false

        if (jump >= 0 && opponentAt(jump)) return false
        if (forward < 0 || side < 0) return true

        if (board.isSquareEmpty(forward)) board.getOccupant(side) != opponentKing
        else if (board.isSquareEmpty(side)) !opponentAt(forward)
        else true
      }

      def checkRearEscape(forward: Int, move: Int, jump: Int, side: Int): Boolean = {
        if (move < 0) return false
        if (jump >= 0 && opponentAt(move) && board.isSquareEmpty(jump)) return true
        if (!board.isSquareEmpty(move)) return false

        if (jump >= 0 && board.getOccupant(jump) == opponentKing) return false
        if (forward < 0 || side < 0) return true

        if (board.isSquareEmpty(forward)) !opponentAt(side)
        else if (board.isSquareEmpty(side)) board.getOccupant(forward) != opponentKing
        else true
      }

      Board.playableSquares.foreach { square =>
        if (board.getOccupant(square) == king) {
          val canEscape =
            checkEscape(
              neighbors.forwardTwo(square),
              neighbors.forwardMoveE(square),
              neighbors.forwardJumpE(square),
              neighbors.twoE(square)) ||
              checkEscape(
                neighbors.forwardTwo(square),
                neighbors.forwardMoveW(square),
                neighbors.forwardJumpW(square),
                neighbors.twoW(square)) ||
              checkRearEscape( //square,
                neighbors.backTwo(square),
                neighbors.backMoveE(square),
                neighbors.backJumpE(square),
                neighbors.twoE(square)) ||
              checkRearEscape( //square,
                neighbors.backTwo(square),
                neighbors.backMoveW(square),
                neighbors.backJumpW(square),
                neighbors.twoW(square))

          if (!canEscape) result += square
        }
      }

      result
    }

    ExpectedSquares(dark = evaluateForSide(DARK, DARKKING, LIGHTKING, neighborTable.Dark),
      light = evaluateForSide(LIGHT, LIGHTKING, DARKKING, neighborTable.Light))
  }


  case class ProbeDataSide(manCount: Int,
                           kingCount: Int,
                           attackMask: Int,
                           safeMask: Int,
                           canEscapeMask: Int,
                           trappedKingMask: Int,
                           attackSet: Set[Int],
                           trappedKingSet: Set[Int])

  case class ProbeData(dark: ProbeDataSide,
                       light: ProbeDataSide,
                       potentialAttackMask: Int,
                       closedNWSet: Set[Int],
                       closedNESet: Set[Int],
                       closedSWSet: Set[Int],
                       closedSESet: Set[Int])

  object ProbeData {
    def fromTestProbe(input: DefaultEvaluatorTestProbe): ProbeData = {
      import Board.squareMaskToSet

      val dark = ProbeDataSide(input.darkManCount,
        input.darkKingCount,
        input.darkAttackMask,
        input.darkSafeMask,
        input.darkCanEscapeMask,
        input.darkTrappedKingMask,
        attackSet = squareMaskToSet(input.darkAttackMask),
        trappedKingSet = squareMaskToSet(input.darkTrappedKingMask))

      val light = ProbeDataSide(input.lightManCount,
        input.lightKingCount,
        input.lightAttackMask,
        input.lightSafeMask,
        input.lightCanEscapeMask,
        input.lightTrappedKingMask,
        attackSet = squareMaskToSet(input.lightAttackMask),
        trappedKingSet = squareMaskToSet(input.lightTrappedKingMask))

      ProbeData(dark, light, input.potentialAttackMask,
        closedNWSet = squareMaskToSet(input.closedNWMask),
        closedNESet = squareMaskToSet(input.closedNEMask),
        closedSWSet = squareMaskToSet(input.closedSWMask),
        closedSESet = squareMaskToSet(input.closedSEMask)
      )
    }
  }

  case class EvaluatorPropInput(board: BoardState,
                                turnToMove: Side,
                                probeData: ProbeData,
                                darkMoves: List[List[Int]],
                                lightMoves: List[List[Int]],
                                expectedAttacks: ExpectedSquares,
                                expectedTrappedKings: ExpectedSquares,
                                boardStats: BoardStats,
                                evaluationResult: Int)

  private lazy val genEvaluatorPropInput: Gen[EvaluatorPropInput] = for {
    turnToMove <- genSide
    board <- genBoard
  } yield {
    val boardStack = BoardStack.fromBoard(board)
    val darkMoves = getMoveList(boardStack, DARK)
    val lightMoves = getMoveList(boardStack, LIGHT)
    val expectedAttacks = getExpectedAttacks(board)
    val expectedTrappedKings = getExpectedTrappedKings(board)
    val boardStats = Board.getBoardStats(board)
    val testProbe = new DefaultEvaluatorTestProbe
    val evaluationResult = evaluator.evaluate(turnToMove, board, testProbe)
    val probeData = ProbeData.fromTestProbe(testProbe)
    EvaluatorPropInput(board, turnToMove, probeData, darkMoves, lightMoves, expectedAttacks, expectedTrappedKings,
      boardStats, evaluationResult)
  }

  private def testProbeCheck(name: String, f: (ProbeData, EvaluatorPropInput) => Boolean): Prop[EvaluatorPropInput] =
    Prop.test(name, { input => f(input.probeData, input) })

  private lazy val darkManCount = testProbeCheck("darkManCount", _.dark.manCount == _.boardStats.darkMan)
  private lazy val lightManCount = testProbeCheck("lightManCount", _.light.manCount == _.boardStats.lightMan)
  private lazy val darkKingCount = testProbeCheck("darkKingCount", _.dark.kingCount == _.boardStats.darkKing)
  private lazy val lightKingCount = testProbeCheck("lightKingCount", _.light.kingCount == _.boardStats.lightKing)

  private lazy val darkAttacks = testProbeCheck("darkAttacks", _.dark.attackSet == _.expectedAttacks.dark)
  private lazy val lightAttacks = testProbeCheck("lightAttacks", _.light.attackSet == _.expectedAttacks.light)

  private lazy val darkTrappedKings = testProbeCheck("darkTrappedKings", _.dark.trappedKingSet == _.expectedTrappedKings.dark)
  private lazy val lightTrappedKings = testProbeCheck("lightTrappedKings", _.light.trappedKingSet == _.expectedTrappedKings.light)

  private lazy val evaluatorPropInputProps = darkManCount & lightManCount & darkKingCount & lightKingCount &
    darkAttacks & lightAttacks & darkTrappedKings & lightTrappedKings


  case class EqualSideCheckPropInput(board: BoardState,
                                     swapped: BoardState,
                                     turnToMove: Side)

  private lazy val genEqualSideCheckPropInput: Gen[EqualSideCheckPropInput] = for {
    turnToMove <- genSide
    board <- genBoard
  } yield {
    val swapped = Board.mirror(board)
    EqualSideCheckPropInput(board, swapped, turnToMove)
  }

  private lazy val sidesEvaluatedEqually: Prop[EqualSideCheckPropInput] = Prop.test("sidesEvaluatedEqually", { input =>
    val score1 = evaluator.evaluate(input.turnToMove, input.board)
    val score2 = evaluator.evaluate(OPPONENT(input.turnToMove), input.swapped)

    val result = score1 == score2
    if(!result) println(s"score1; $score1,  score2: $score2")
    result
  })


  val tests: Tests = Tests {
    'Evaluator {
      genEvaluatorPropInput.mustSatisfy(evaluatorPropInputProps)
      genEqualSideCheckPropInput.mustSatisfy(sidesEvaluatedEqually)
    }

  }

}
