package checkers.benchmark

import org.scalajs.dom
import checkers.core._
import checkers.consts._
import checkers.core.old.MoveGenerator
import checkers.game.TestStates
import dom.window.performance



object MoveGeneratorBenchmarks {

  val rulesSettings = RulesSettings.default
  val executor = new MoveExecutor(rulesSettings)
  val generator = new MoveGenerator(rulesSettings, executor)

  def testBoard(repetitions: Int)(boardState: BoardStack, turnToMove: Color): Double = {
    val startTime = performance.now()
    var i = repetitions
    while (i > 0) {
      generator.generateMoves(boardState, turnToMove)
      i -= 1
    }

    val endTime = performance.now()
    endTime - startTime
  }



  def test1(): Unit = {
    val board = RulesSettings.initialBoard(rulesSettings)
    val stack = BoardStack.fromBoard(board)
    val t = testBoard(100000)(BoardStack.fromBoard(board), DARK)
    println(s"test1: $t")

    println(EMPTY)
    println(DARK)
    println(LIGHT)
    println(MAN)
    println(KING)

    println(TestStates.board1)

  }

}