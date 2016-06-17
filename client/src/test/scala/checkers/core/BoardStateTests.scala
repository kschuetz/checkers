package checkers.core


import checkers.consts._
import utest._
import utest.framework.Test
import utest.util.Tree

import scala.util.Random

object BoardStateTests extends TestSuite {

  val allPieces = List(LIGHTMAN, DARKMAN, LIGHTKING, DARKKING)
  val allSquares = Board.allSquares.toSet

  private def shuffledSquares() = Random.shuffle(Board.allSquares.toList)

  private def randomSquares(count: Int) = shuffledSquares().take(count)


  override def tests: Tree[Test] = TestSuite {
    'BoardState {
      'PlacePieces {
        val squares = randomSquares(4)
        val placements = squares.zip(allPieces)
        val bs = placements.foldLeft(BoardState.empty){
          case (result, (square, piece)) => result.updated(square, piece)
        }

        // pieces in correct place
        placements.foreach {
          case (square, piece) =>
            assert(bs.getOccupant(square) == piece)
        }

        // all other squares are empty
        (allSquares -- squares.toSet).foreach { square =>
          assert(bs.isSquareEmpty(square))
        }
      }
    }
  }
}