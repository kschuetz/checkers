package checkers.masks

import checkers.consts._
import checkers.core._
import checkers.test.TestSuiteBase
import checkers.test.generators.BoardGenerators
import nyaya.gen._
import nyaya.prop._
import nyaya.test.PropTest._
import utest._
import utest.framework._

object ShiftTests extends TestSuiteBase with BoardGenerators {

  sealed trait Shift {
    def complement: Shift

    def rowOffset: Int

    def colOffset: Int

    def applyShift(x: Int): Int

    def newRow: Int = if (rowOffset < 0) 7 else 0

    def newCol: Int = if (colOffset < 0) 7 else 0
  }

  case object ShiftNW extends Shift {
    def complement: Shift = ShiftSE

    def rowOffset: Int = -1

    def colOffset: Int = -1

    def applyShift(x: Int) = SHIFTNW(x)
  }

  case object ShiftNE extends Shift {
    def complement: Shift = ShiftSW

    def rowOffset: Int = -1

    def colOffset: Int = 1

    def applyShift(x: Int) = SHIFTNE(x)
  }

  case object ShiftSW extends Shift {
    def complement: Shift = ShiftNE

    def rowOffset: Int = 1

    def colOffset: Int = -1

    def applyShift(x: Int) = SHIFTSW(x)
  }

  case object ShiftSE extends Shift {
    def complement: Shift = ShiftNW

    def rowOffset: Int = 1

    def colOffset: Int = 1

    def applyShift(x: Int) = SHIFTSE(x)
  }

  case object ShiftN extends Shift {
    def complement: Shift = ShiftS

    def rowOffset: Int = -2

    def colOffset: Int = 0

    def applyShift(x: Int) = SHIFTN(x)
  }

  case object ShiftS extends Shift {
    def complement: Shift = ShiftN

    def rowOffset: Int = 2

    def colOffset: Int = 0

    def applyShift(x: Int) = SHIFTS(x)
  }

  case object ShiftE extends Shift {
    def complement: Shift = ShiftW

    def rowOffset: Int = 0

    def colOffset: Int = 2

    def applyShift(x: Int) = SHIFTE(x)
  }

  case object ShiftW extends Shift {
    def complement: Shift = ShiftE

    def rowOffset: Int = 0

    def colOffset: Int = -2

    def applyShift(x: Int) = SHIFTW(x)
  }

  case object ShiftNW2 extends Shift {
    def complement: Shift = ShiftSE2

    def rowOffset: Int = -2

    def colOffset: Int = -2

    def applyShift(x: Int) = SHIFTNW2(x)
  }

  case object ShiftNE2 extends Shift {
    def complement: Shift = ShiftSW2

    def rowOffset: Int = -2

    def colOffset: Int = 2

    def applyShift(x: Int) = SHIFTNE2(x)
  }

  case object ShiftSW2 extends Shift {
    def complement: Shift = ShiftNE2

    def rowOffset: Int = 2

    def colOffset: Int = -2

    def applyShift(x: Int) = SHIFTSW2(x)
  }

  case object ShiftSE2 extends Shift {
    def complement: Shift = ShiftNW2

    def rowOffset: Int = 2

    def colOffset: Int = 2

    def applyShift(x: Int) = SHIFTSE2(x)
  }

  private def applyShiftMacro(boardState: MutableBoardState, shift: Shift): Unit = {
    val dp = shift.applyShift(boardState.darkPieces)
    val lp = shift.applyShift(boardState.lightPieces)
    val k = shift.applyShift(boardState.kings)
    boardState.setDarkPieces(dp)
    boardState.setLightPieces(lp)
    boardState.setKings(k)
  }

  private def alternateApplyShift(boardState: MutableBoardState, shift: Shift): Unit = {
    val sourceBoard = boardState.toImmutable
    val dy = -shift.rowOffset
    val dx = -shift.colOffset
    for (row <- 0 to 7; col <- 0 to 7) {
      val destSquare = BoardPosition(row, col).toSquareIndex
      if (destSquare >= 0) {
        val sourceSquare = BoardPosition(row + dy, col + dx).toSquareIndex
        val sourceOccupant = if (sourceSquare >= 0) sourceBoard.getOccupant(sourceSquare) else EMPTY
        boardState.setOccupant(destSquare, sourceOccupant)
      }
    }
  }

  case class AppliedShifts(forward1: BoardState,
                           forward1back1: BoardState,
                           forward2: BoardState,
                           forward2back1: BoardState,
                           forward2back2: BoardState)

  def applyShifts(f: (MutableBoardState, Shift) => Unit)(center: BoardStack, shift: Shift): AppliedShifts = {
    val complement = shift.complement

    center.push()
    f(center, shift)
    val forward1 = center.toImmutable
    center.pop()

    center.push()
    f(center, shift)
    f(center, complement)
    val forward1back1 = center.toImmutable
    center.pop()

    center.push()
    f(center, shift)
    f(center, shift)
    val forward2 = center.toImmutable
    center.pop()

    center.push()
    f(center, shift)
    f(center, shift)
    f(center, complement)
    val forward2back1 = center.toImmutable
    center.pop()

    center.push()
    f(center, shift)
    f(center, shift)
    f(center, complement)
    f(center, complement)
    val forward2back2 = center.toImmutable
    center.pop()

    AppliedShifts(forward1, forward1back1, forward2, forward2back1, forward2back2)
  }

  private lazy val getShiftsWithMacros = applyShifts(applyShiftMacro) _
  private lazy val getAlternateShifts = applyShifts(alternateApplyShift) _


  private lazy val genShift: Gen[Shift] = Gen.choose(ShiftN, ShiftS, ShiftE, ShiftW,
    ShiftNW, ShiftNE, ShiftSW, ShiftSE,
    ShiftNW2, ShiftNE2, ShiftSW2, ShiftSE2)


  case class ShiftMacroTestInput(shift: Shift,
                                 center: BoardState,
                                 shiftsWithMacros: AppliedShifts,
                                 alternateShifts: AppliedShifts)

  lazy val genTestInput: Gen[ShiftMacroTestInput] = for {
    center <- genBoard
    shift <- genShift
  } yield {
    val boardStack = BoardStack.fromBoard(center)
    val shiftsWithMacros = getShiftsWithMacros(boardStack, shift)
    val alternateShifts = getAlternateShifts(boardStack, shift)
    ShiftMacroTestInput(shift, center, shiftsWithMacros, alternateShifts)
  }

  private def boardsEqualProp(name: String, pluckBoard: AppliedShifts => BoardState): Prop[ShiftMacroTestInput] =
    Prop.test(name, { input =>
      val board1 = pluckBoard(input.shiftsWithMacros)
      val board2 = pluckBoard(input.alternateShifts)
      Board.boardStatesEqual(board1, board2)
    })

  private lazy val forward1Equal = boardsEqualProp("forward1Equal", _.forward1)
  private lazy val forward1back1Equal = boardsEqualProp("forward1back1Equal", _.forward1back1)
  private lazy val forward2Equal = boardsEqualProp("forward2Equal", _.forward2)
  private lazy val forward2back1Equal = boardsEqualProp("forward2back1Equal", _.forward2back1)
  private lazy val forward2back2Equal = boardsEqualProp("forward2back2Equal", _.forward2back2)

  private lazy val props = forward1Equal & forward1back1Equal & forward2Equal & forward2back1Equal & forward2back2Equal

  override def tests: Tree[Test] = TestSuite {
    'ShiftMacros {
      genTestInput.mustSatisfy(props)
    }
  }
}