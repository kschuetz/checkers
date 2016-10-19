package checkers.computer

import checkers.test.TestSuiteBase
import nyaya.gen._
import nyaya.prop._
import nyaya.test.PropTest._
import utest._
import utest.framework._

object PrincipalVariationTests extends TestSuiteBase {

  case class TestObject(n: Int)

  case class TestCase(depth: Int, items: Vector[TestObject])

  lazy val genTestObject: Gen[TestObject] = Gen.choose(1, 100).map(TestObject)

  lazy val genTestCase: Gen[TestCase] = for {
    depth <- Gen.choose(3, 16)
    items <- genTestObject.list.take(depth)
  } yield TestCase(depth, items.toVector)

  lazy val works = Prop.test[TestCase]("works", { testCase =>
    val pv = new PrincipalVariation[Int](testCase.depth)
    val input = testCase.items.map(_.n)
    input.foreach { idx =>
      pv.updateBestMove(idx, input(idx))
    }
    val output = testCase.items.indices.map(idx => pv.getBestMove(idx)).toVector
    input == output
  })

  override def tests: Tree[Test] = TestSuite {
    'PrincipalVariation {
      genTestCase.mustSatisfy(works)
    }

  }

}