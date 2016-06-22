package checkers.core

import checkers.consts._
import checkers.test.BoardUtils
import utest._
import utest.framework.Test
import utest.util.Tree

object MoveGeneratorTests extends TestSuite {

  private val moveGenerator = new MoveGenerator(RulesSettings.default)


  private def testBoard(board: BoardState, color: Color, expectedResult: Set[List[Int]]): Unit = {
    val stack = BoardStack.fromBoard(board)
    val result = moveGenerator.generateMoves(stack, color).toSet
    assert(result == expectedResult)
  }


  //           28  29  30  31
  //         24  25  26  27
  //           20  21  22  23
  //         16  17  18  19
  //           12  13  14  15
  //         08  09  10  11
  //           04  05  06  07
  //         00  01  02  03



  // simpleMove
  private def s(pair: (Int, Int)): List[Int] =
    List(pair._1, pair._2)

  override def tests: Tree[Test] = TestSuite {
    'MoveGenerator {
      'EmptyBoard {
        'Dark {
          testBoard(BoardState.empty, DARK, Set.empty)
        }
        'Light {
          testBoard(BoardState.empty, LIGHT, Set.empty)
        }
      }

      'OpeningBoard {
        val board = BoardState.create(DARKMAN -> (0 to 11), LIGHTMAN -> (20 to 31))

        'Dark {
          testBoard(board, DARK, Set(
            s(8 -> 12),
            s(9 -> 12),
            s(9 -> 13),
            s(10 -> 13),
            s(10 -> 14),
            s(11 -> 14),
            s(11 -> 15)))
        }

        'Light {
          testBoard(board, LIGHT, Set(
            s(20 -> 16),
            s(20 -> 17),
            s(21 -> 17),
            s(21 -> 18),
            s(22 -> 18),
            s(22 -> 19),
            s(23 -> 19)))
        }
      }

      'Board1 {
        val board = BoardUtils.parseBoard(
          """
              l l - -
             - - - -
              - - - -
             - l - -
              - l l -
             d d d d
              l - - -
             - - - -
          """)

        'Dark {
          testBoard(board, DARK, Set(
            s(9 -> 18),
            s(10 -> 19),
            s(11 -> 18)
          ))
        }

        'Light {
          testBoard(board, LIGHT, Set(
            s(13 -> 6),
            s(14 -> 5),
            s(14 -> 7)
          ))
        }
      }
    }
  }
}
