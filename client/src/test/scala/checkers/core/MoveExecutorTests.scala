package checkers.core

import checkers.consts._
import checkers.test.generators.{BoardGenerators, BoardWithMovesGenerators, ColorGenerator}
import checkers.test.{BoardUtils, DefaultGameLogicTestModule}
import utest._
import utest.framework._
import nyaya.gen._
import nyaya.prop._
import nyaya.test._
import nyaya.test.PropTest._

object MoveExecutorTests extends TestSuite
  with DefaultGameLogicTestModule
  with ColorGenerator
  with BoardWithMovesGenerators {

  lazy val moveGenerator = gameLogicModule.moveGenerator
  lazy val moveDecoder = new MoveDecoder


  override def tests: Tree[Test] = TestSuite {


  }
}