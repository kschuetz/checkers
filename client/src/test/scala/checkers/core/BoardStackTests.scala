package checkers.core

import checkers.test.TestSuiteBase
import checkers.test.generators.BoardGenerators
import nyaya.gen._
import nyaya.prop._
import nyaya.test.PropTest._
import utest._
import utest.framework._

object BoardStackTests extends TestSuiteBase
  with BoardGenerators {

  case class PropInput(board: BoardStateRead,
                       startLevel: Int)

  private def makeStack(startLevel: Int): BoardStack = {
    val result = BoardStack.fromBoard(BoardState.empty)
    for (_ <- 0 until startLevel) {
      result.push()
    }
    result
  }

  private def mutateBoard(stack: BoardStack): Unit = {
    val k = stack.kings << 1
    val dp = stack.darkPieces << 1
    val lp = stack.lightPieces << 1
    stack.setKings(k)
    stack.setDarkPieces(dp)
    stack.setLightPieces(lp)
  }

  lazy val genInput: Gen[PropInput] = for {
    board <- genBoard
    startLevel <- Gen.chooseInt(10)
  } yield PropInput(board, startLevel)

  lazy val setBoardWorks: Prop[PropInput] = Prop.test("setBoardWorks", { input =>
    val stack = makeStack(input.startLevel)
    stack.setBoard(input.board)

    val sample = stack.toImmutable
    Board.boardStatesEqual(input.board, sample)
  })

  lazy val pushCopiesCurrentBoard: Prop[PropInput] = Prop.test("pushCopiesCurrentBoard", { input =>
    val stack = makeStack(input.startLevel)
    stack.setBoard(input.board)
    stack.push()

    val sample = stack.toImmutable
    Board.boardStatesEqual(input.board, sample)
  })

  lazy val popRestoresBoard: Prop[PropInput] = Prop.test("popRestoresBoard", { input =>
    val stack = makeStack(input.startLevel)
    stack.setBoard(input.board)
    stack.push()
    mutateBoard(stack)
    val sampleA = stack.toImmutable
    stack.push()
    stack.pop()
    val sampleB = stack.toImmutable
    stack.pop()
    val sample = stack.toImmutable
    Board.boardStatesEqual(input.board, sample) && Board.boardStatesEqual(sampleA, sampleB)
  })

  lazy val stackLevel: Prop[PropInput] = Prop.test("stackLevel", { input =>
    val stack = makeStack(input.startLevel)
    val level = stack.level
    for (_ <- 0 until level) stack.pop()
    (stack.level == 0) && (level == input.startLevel)
  })

  override def tests: Tree[Test] = TestSuite {
    'BoardStack {
      'Properties {
        'setBoardWorks {
          genInput.mustSatisfy(setBoardWorks)
        }
        'pushCopiesCurrentBoard {
          genInput.mustSatisfy(pushCopiesCurrentBoard)
        }
        'popRestoresBoard {
          genInput.mustSatisfy(popRestoresBoard)
        }
        'stackLevel {
          genInput.mustSatisfy(stackLevel)
        }
      }
    }
  }
}