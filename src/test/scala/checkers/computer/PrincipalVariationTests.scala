package checkers.computer

import checkers.test.TestSuiteBase
import nyaya.gen._
import nyaya.prop._
import nyaya.test.PropTest._
import utest._

object PrincipalVariationTests extends TestSuiteBase {

  case class TestObject(n: Int)

  case class TestCase(depth: Int, items: Vector[TestObject])

  private lazy val genTestObject: Gen[TestObject] = Gen.choose(1, 100).map(TestObject)

  private lazy val genTestCase: Gen[TestCase] = for {
    depth <- Gen.choose(3, 16)
    items <- genTestObject.fill(depth)
  } yield TestCase(depth, items.toVector)

  private lazy val works = Prop.test[TestCase]("works", { testCase =>
    val pv = new PrincipalVariation[TestObject](testCase.depth)
    val input = testCase.items
    val indices = input.indices
    indices.reverse.foreach { idx =>
      pv.updateBestMove(idx, input(idx))
    }
    val output = indices.map(idx => pv.getBestMove(idx)).toVector
    input == output
  })

  val tests: Tests = Tests {
    test("PrincipalVariation") {
      genTestCase.mustSatisfy(works)
    }

  }

}
