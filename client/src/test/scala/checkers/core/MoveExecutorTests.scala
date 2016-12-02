package checkers.core

import checkers.consts._
import checkers.core.tables.JumpTable
import checkers.test.generators.{BoardWithMovesGenerators, SideGenerator}
import checkers.test.{BoardUtils, DefaultGameLogicTestModule, TestSuiteBase}
import nyaya.gen._
import nyaya.prop._
import nyaya.test.PropTest._
import utest._
import utest.framework._

object MoveExecutorTests extends TestSuiteBase
  with DefaultGameLogicTestModule
  with SideGenerator
  with BoardWithMovesGenerators {

  protected lazy val moveGenerator: MoveGenerator = gameLogicModule.moveGenerator
  protected lazy val moveExecutor: MoveExecutor = gameLogicModule.moveExecutor
  protected lazy val moveDecoder: MoveDecoder = new MoveDecoder
  protected lazy val jumpTable: JumpTable = tablesModule.jumpTable

  case class MoveExecutorPropInput(moveWasMade: Boolean,
                                   before: BoardStateRead,
                                   after: BoardStateRead,
                                   turnToMove: Side,
                                   path: List[Int],
                                   reversePath: List[Int])


  private def makePropInputGen(makeMove: (BoardStack, Side, List[Int]) => Unit): Gen[MoveExecutorPropInput] =
    genBoardWithMove.map {
      case BoardWithMove(boardStack, turnToMove, Some(legalMove)) =>
        val before = boardStack.toImmutable
        boardStack.push()
        makeMove(boardStack, turnToMove, legalMove)
        val after = boardStack.toImmutable
        boardStack.pop()
        MoveExecutorPropInput(moveWasMade = true, before, after, turnToMove, path = legalMove, reversePath = legalMove.reverse)
      case BoardWithMove(boardStack, turnToMove, None) =>
        MoveExecutorPropInput(moveWasMade = false, boardStack, boardStack, turnToMove, Nil, Nil)
    }

  private def makeMoveWithMoveDecoder(boardStack: BoardStack, turnToMove: Side, path: List[Int]): Unit = {
    moveDecoder.loadFromList(path)
    moveExecutor.executeFromMoveDecoder(boardStack, moveDecoder)
  }

  private def makeMoveWithFastExecute(boardStack: BoardStack, turnToMove: Side, path: List[Int]): Unit = {
    var from :: remaining = path
    while(remaining.nonEmpty) {
      val to :: next = remaining
      moveExecutor.fastExecute(boardStack, from, to)
      from = to
      remaining = next
    }
  }

  private def makeMoveWithExecute(boardStack: BoardStack, turnToMove: Side, path: List[Int]): Unit = {
    var from :: remaining = path
    while(remaining.nonEmpty) {
      val to :: next = remaining
      moveExecutor.execute(boardStack, from, to)
      from = to
      remaining = next
    }
  }


  private lazy val genInputWithMoveDecoder = makePropInputGen(makeMoveWithMoveDecoder)

  private lazy val genInputWithFastExecute = makePropInputGen(makeMoveWithFastExecute)

  private lazy val genInputWithExecute = makePropInputGen(makeMoveWithExecute)

  private lazy val startSquareCorrectState: Prop[MoveExecutorPropInput] = Prop.test("startSquareCorrectState", {
    case MoveExecutorPropInput(true, before, after, turnToMove, path, reversePath) =>
      val startSquare = path.head
      val endSquare = reversePath.head
      if(startSquare == endSquare) {
        // rare case of compound jump ending on same square
        after.squareHasSide(turnToMove)(endSquare)
      } else {
        after.isSquareEmpty(startSquare)
      }
    case _ => true
  })

  private lazy val endSquareCorrectState: Prop[MoveExecutorPropInput] = Prop.test("endSquareCorrectState", {
    case MoveExecutorPropInput(true, before, after, turnToMove, path, reversePath) =>
      val endSquare = reversePath.head
     after.squareHasSide(turnToMove)(endSquare)
    case _ => true
  })

  private lazy val crownedAppropriately: Prop[MoveExecutorPropInput] = Prop.test("crownedAppropriately", {
    case MoveExecutorPropInput(true, before, after, turnToMove, path, reversePath) =>
      val startSquare = path.head
      val endSquare = reversePath.head
      val startPieceType = PIECETYPE(before.getOccupant(startSquare))
      val endPieceType = PIECETYPE(after.getOccupant(endSquare))

      if(startPieceType == KING) {
        endPieceType == KING
      } else {
        if(Board.crowningSquares(turnToMove).contains(endSquare)) {
          endPieceType == KING
        } else {
          endPieceType == MAN
        }
      }
    case _ => true
  })

  private lazy val jumpedOverSquaresEmpty: Prop[MoveExecutorPropInput] = Prop.test("jumpedOverSquaresEmpty", {
    case MoveExecutorPropInput(true, before, after, turnToMove, path, reversePath) =>
      val jumpedOver = jumpTable.getMiddles(path)
      jumpedOver.forall(after.isSquareEmpty)
    case _ => true
  })

  private lazy val allMethodsSameOutcome: Prop[BoardWithMove] = Prop.test("allMethodsSameOutcome", {
    case BoardWithMove(board, turnToMove, Some(path)) =>
      board.push()
      makeMoveWithMoveDecoder(board, turnToMove, path)
      val board1 = board.toImmutable
      board.pop()
      board.push()
      makeMoveWithFastExecute(board, turnToMove, path)
      val board2 = board.toImmutable
      board.pop()
      board.push()
      makeMoveWithExecute(board, turnToMove, path)
      val board3 = board.toImmutable
      board.pop()
      BoardUtils.boardStatesEqual(board1, board2) && BoardUtils.boardStatesEqual(board2, board3)
    case _ => true
  })

  private lazy val executeMoveProps = startSquareCorrectState & endSquareCorrectState & crownedAppropriately & jumpedOverSquaresEmpty

  override def tests: Tree[Test] = TestSuite {
    'MoveExecutor {
      'Properties {
        'executeFromMoveDecoder {
          genInputWithMoveDecoder.mustSatisfy(executeMoveProps)
        }
        'fastExecute {
          genInputWithFastExecute.mustSatisfy(executeMoveProps)
        }
        'execute {
          genInputWithExecute.mustSatisfy(executeMoveProps)
        }
        'allMethodsSameOutcome {
          genBoardWithMove.mustSatisfy(allMethodsSameOutcome)
        }
      }
    }
  }
}