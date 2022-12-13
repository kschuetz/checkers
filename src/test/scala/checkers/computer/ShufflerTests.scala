package checkers.computer

import checkers.test.TestSuiteBase
import checkers.test.generators.ComputerPlayerStateGenerator
import nyaya.gen._
import nyaya.prop._
import nyaya.test.PropTest._
import utest._

object ShufflerTests extends TestSuiteBase with ComputerPlayerStateGenerator {

  private val factory = new DefaultShufflerFactory

  private val levelsToTest = List(1, 2, 3, 4, 9, 16, 25, 36)

  case class TestCase(shuffler: Shuffler,
                      plyIndex: Int,
                      pvMoveInFront: Boolean)

  lazy val genTestCase: Gen[TestCase] = for {
    cps <- genComputerPlayerState
    (shuffler, _) = factory.createShuffler(cps)
    plyIndex <- Gen.choose(0, 20)
    pvMoveInFront <- Gen.frequency[Boolean]((3, Gen.pure(false)), (1, Gen.pure(true)))
  } yield TestCase(shuffler, plyIndex, pvMoveInFront)

  private def allIndicesPresentProp(level: Int): Prop[TestCase] = {
    val allIndices = 0 until level
    val expected = allIndices.toSet
    Prop.test[TestCase](s"allIndicesPresent-$level", { testCase =>
      val actualIndices = allIndices.map { inputIndex =>
        testCase.shuffler.getMoveIndex(inputIndex, level, testCase.plyIndex, testCase.pvMoveInFront)
      }.toSet

      actualIndices == expected
    })
  }

  private def respectPvMoveProp(level: Int): Prop[TestCase] = {
    Prop.test[TestCase](s"respectPvMove-$level", { testCase =>
      if(testCase.pvMoveInFront) {
        val actual = testCase.shuffler.getMoveIndex(0, level, testCase.plyIndex, testCase.pvMoveInFront)
        actual == 0
      } else true
    })
  }

  private lazy val allIndicesPresent: Prop[TestCase] = levelsToTest.map(allIndicesPresentProp).reduceRight(_ & _)
  private lazy val respectPvMove: Prop[TestCase] = levelsToTest.map(respectPvMoveProp).reduceRight(_ & _)


  val tests: Tests = Tests {
    test("Shuffler") {
      genTestCase.mustSatisfy(allIndicesPresent)
      genTestCase.mustSatisfy(respectPvMove)
    }
  }

}
