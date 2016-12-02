package checkers.core

import checkers.test.generators.{BoardWithMovesGenerators, SideGenerator}
import checkers.test.{DefaultGameLogicTestModule, TestSuiteBase}
import nyaya.gen._
import nyaya.prop._
import nyaya.test.PropTest._
import utest._
import utest.framework._

object MoveListTests extends TestSuiteBase
  with DefaultGameLogicTestModule
  with SideGenerator
  with BoardWithMovesGenerators {

  protected lazy val moveDecoder: MoveDecoder = new MoveDecoder
  protected lazy val moveGenerator: MoveGenerator = gameLogicModule.moveGenerator

  case class MoveListPropInput(moves: MoveList,
                               paths: Vector[List[Int]],
                               pathSet: Set[List[Int]],
                               indices: Set[Int])


  private lazy val genInput: Gen[MoveListPropInput] = genBoardWithMoves.map { boardWithMoves =>
    val moves = boardWithMoves.legalMoves
    val paths = moves.toList
    val indices = paths.map(path => moves.indexOf(path, moveDecoder)).toSet
    MoveListPropInput(moves, paths.toVector, paths.toSet, indices)
  }


  private lazy val uniqueIndicesProp: Prop[MoveListPropInput] = Prop.test("uniqueIndices", { input =>
    input.indices.size == input.paths.size
  })

  private lazy val allPathsFound: Prop[MoveListPropInput] = Prop.test("allPathsFound", { input =>
    input.indices.forall(_ >= 0)
  })

  private lazy val indexOfProps = uniqueIndicesProp & allPathsFound

  private lazy val moveFirstToFrontProp: Prop[MoveListPropInput] = Prop.test("moveFirstToFront", { input =>
    if(input.paths.nonEmpty) {
      val path = input.paths.head
      val after = input.moves.moveToFrontIfExists(path).getOrElse(input.moves)
      val afterPaths = after.toList.toVector
      afterPaths == input.paths
    } else true
  })


  private def moveNToFrontProp(n: Int, name: String): Prop[MoveListPropInput] = Prop.test(name, { input =>
    if(input.paths.size > n) {
      val path = input.paths(n)
      val after = input.moves.moveToFrontIfExists(path).getOrElse(input.moves)
      val afterPaths = after.toList.toVector

      afterPaths.head == path && afterPaths.toSet == input.pathSet
    } else true
  })

  private lazy val moveSecondToFrontProp: Prop[MoveListPropInput] = moveNToFrontProp(1, "moveSecondToFront")
  private lazy val moveThirdToFrontProp: Prop[MoveListPropInput] = moveNToFrontProp(2, "moveThirdToFront")

  private lazy val moveIllegalToFrontProp: Prop[MoveListPropInput] = Prop.test("moveIllegalToFront", { input =>
    val illegalPath = List(0, 4)
    val after = input.moves.moveToFrontIfExists(illegalPath).getOrElse(input.moves)
    val afterPaths = after.toList.toVector
    afterPaths == input.paths
  })

  private lazy val moveToFrontIfExistsProps = moveFirstToFrontProp & moveSecondToFrontProp & moveThirdToFrontProp & moveIllegalToFrontProp

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