package checkers.core

import checkers.consts._
import checkers.test.BoardUtils
import nyaya.gen.Gen
import utest._
import utest.framework.Test
import utest.util.Tree
import nyaya.prop._
import nyaya.test.PropTest._

object MoveGeneratorTests extends TestSuite {

  import checkers.test.BoardGenerators._

  private val moveGenerator = new MoveGenerator(RulesSettings.default)


  private def getJumpersDark(board: BoardState): Set[Int] =
    BoardUtils.squareMaskToSet(moveGenerator.getJumpersDark(board))

  private val jumpersDarkCorrect: Prop[(BoardState, Set[Int])] = Prop.test("jumpersDarkCorrect", { case (board, result) =>
    result.forall(BoardUtils.isJumperOfColor(board, DARK))
  })

  private val genJumpersDarkInput: Gen[(BoardState, Set[Int])] = for {
    board <- genBoard
  } yield (board, getJumpersDark(board))

  override def tests: Tree[Test] = TestSuite {
    'MoveGenerator {
      'getJumpersDark {
        genJumpersDarkInput.mustSatisfy(jumpersDarkCorrect)
      }
    }
  }
}
