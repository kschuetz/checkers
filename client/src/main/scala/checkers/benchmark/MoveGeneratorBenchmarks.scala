package checkers.benchmark

import org.scalajs.dom
import checkers.core._
import checkers.consts._
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
    val t = testBoard(500000)(BoardStack.fromBoard(board), Dark)
    println(s"test1: $t")

    println(Empty)
    println(Dark)
    println(Light)
    println(Man)
    println(King)

    println(TestStates.board1)

  }

}