package checkers.core

import checkers.consts._
import checkers.test.generators.{BoardGenerators, BoardWithMovesGenerators, ColorGenerator}
import checkers.test.{BoardUtils, DefaultGameLogicTestModule, TestSuiteBase}
import utest._
import utest.framework._
import nyaya.gen._
import nyaya.prop._
import nyaya.test._
import nyaya.test.PropTest._

object MoveExecutorTests extends TestSuiteBase
  with DefaultGameLogicTestModule
  with ColorGenerator
  with BoardWithMovesGenerators {

  lazy val moveGenerator = gameLogicModule.moveGenerator
  lazy val moveExecutor = gameLogicModule.moveExecutor
  lazy val moveDecoder = new MoveDecoder

  case class MoveExecutorPropInput(moveWasMade: Boolean,
                                   before: BoardStateRead,
                                   after: BoardStateRead,
                                   turnToMove: Color,
                                   path: List[Int],
                                   reversePath: List[Int])

  lazy val genInputWithMoveDecoder: Gen[MoveExecutorPropInput] = genBoardWithMove.map {
    case BoardWithMove(boardStack, turnToMove, Some(legalMove)) =>
      val before = boardStack.toImmutable
      boardStack.push()
      moveDecoder.loadFromList(legalMove)
      moveExecutor.executeFromMoveDecoder(boardStack, moveDecoder)
      val after = boardStack.toImmutable
      boardStack.pop()
      MoveExecutorPropInput(moveWasMade = true, before, after, turnToMove, path = legalMove, reversePath = legalMove.reverse)
    case BoardWithMove(boardStack, turnToMove, None) =>
      MoveExecutorPropInput(moveWasMade = false, boardStack, boardStack, turnToMove, Nil, Nil)
  }

  lazy val endSquareContainsCurrentPlayer: Prop[MoveExecutorPropInput] = Prop.test("endSquareContainsCurrentPlayer", {
    case MoveExecutorPropInput(true, before, after, turnToMove, legalMove, reversePath) =>
      val endSquare = reversePath.head
     after.squareHasColor(turnToMove)(endSquare)
    case _ => true
  })

  override def tests: Tree[Test] = TestSuite {
    'MoveExecutor {
      'Properties {
        'executeFromMoveDecoder {
          genInputWithMoveDecoder.mustSatisfy(
            endSquareContainsCurrentPlayer
          )
        }
      }
    }
  }
}