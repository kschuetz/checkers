package checkers.core

import checkers.test.generators.{BoardWithMovesGenerators, ColorGenerator}
import checkers.test.{DefaultGameLogicTestModule, TestSuiteBase}
import nyaya.gen._
import nyaya.prop._
import nyaya.test.PropTest._
import utest._
import utest.framework._

object MoveListTests extends TestSuiteBase
  with DefaultGameLogicTestModule
  with ColorGenerator
  with BoardWithMovesGenerators {

  lazy val moveDecoder = new MoveDecoder
  lazy val moveGenerator = gameLogicModule.moveGenerator

  case class MoveListPropInput(moves: MoveList,
                               paths: Vector[List[Int]],
                               pathSet: Set[List[Int]],
                               indices: Set[Int])


  lazy val genInput: Gen[MoveListPropInput] = genBoardWithMoves.map { boardWithMoves =>
    val moves = boardWithMoves.legalMoves
    val paths = moves.toList
    val indices = paths.map(path => moves.indexOf(path, moveDecoder)).toSet
    MoveListPropInput(moves, paths.toVector, paths.toSet, indices)
  }


  lazy val uniqueIndicesProp: Prop[MoveListPropInput] = Prop.test("uniqueIndices", { input =>
    input.indices.size == input.paths.size
  })

  lazy val allPathsFound: Prop[MoveListPropInput] = Prop.test("allPathsFound", { input =>
    input.indices.forall(_ >= 0)
  })

  lazy val indexOfProps = uniqueIndicesProp & allPathsFound

  lazy val moveFirstToFrontProp: Prop[MoveListPropInput] = Prop.test("moveFirstToFront", { input =>
    if(input.paths.nonEmpty) {
      val path = input.paths.head
      val after = input.moves.moveToFrontIfExists(path)
      val afterPaths = after.toList.toVector
      afterPaths == input.paths
    } else true
  })


  private def moveNToFrontProp(n: Int, name: String): Prop[MoveListPropInput] = Prop.test(name, { input =>
    if(input.paths.size > n) {
      val path = input.paths(n)
      val after = input.moves.moveToFrontIfExists(path)
      val afterPaths = after.toList.toVector

      afterPaths.head == path && afterPaths.toSet == input.pathSet
    } else true
  })

  lazy val moveSecondToFrontProp: Prop[MoveListPropInput] = moveNToFrontProp(1, "moveSecondToFront")
  lazy val moveThirdToFrontProp: Prop[MoveListPropInput] = moveNToFrontProp(2, "moveThirdToFront")

  lazy val moveIllegalToFrontProp: Prop[MoveListPropInput] = Prop.test("moveIllegalToFront", { input =>
    val illegalPath = List(0, 4)
    val after = input.moves.moveToFrontIfExists(illegalPath)
    val afterPaths = after.toList.toVector
    afterPaths == input.paths
  })

  lazy val moveToFrontIfExistsProps = moveFirstToFrontProp & moveSecondToFrontProp & moveThirdToFrontProp & moveIllegalToFrontProp

  override def tests: Tree[Test] = TestSuite {
    'MoveExecutor {
      'Properties {
        'indexOf {
          genInput.mustSatisfy(indexOfProps)
        }

        'moveToFrontIfExists {
          genInput.mustSatisfy(moveToFrontIfExistsProps)
        }
      }
    }
  }
}