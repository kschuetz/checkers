package checkers.test.generators

import checkers.consts._
import checkers.core.BoardState
import nyaya.gen.Gen

trait BoardGenerators {
  // counts for (men, kings)
  protected lazy val choosePieceCount: Gen[(Int, Int)] = Gen.chooseInt(12).flatMap { totalPieces =>
    if(totalPieces <= 0) Gen.pure((0, 0))
    else Gen.chooseInt(totalPieces).map { kingCount =>
      (totalPieces - kingCount, kingCount)
    }
  }

  protected lazy val boardPositions: Gen[List[Int]] =
    Gen.shuffle((0 to 31).toList)

  private def updateBoard(board: BoardState, occupants: List[(Int, Occupant)], positions: List[Int]): BoardState = {
    occupants match {
      case Nil => board
      case (count, occupant) :: rest =>
        val newBoard = board.updateMany(occupant)(positions.take(count))
        updateBoard(newBoard, rest, positions.drop(count))
    }
  }

  protected lazy val genBoard: Gen[BoardState] = for {
    (lightMen, lightKings) <- choosePieceCount
    (darkMen, darkKings) <- choosePieceCount
    positions <- boardPositions
  } yield {
    val board = updateBoard(BoardState.empty, List(lightMen -> LIGHTMAN, darkMen -> DARKMAN, lightKings -> LIGHTKING, darkKings -> DARKKING), positions)
    normalizeBoard(board)
  }


  private def normalizeBoard(board: BoardState): BoardState = {
    var result = board
    var i = 0
    while(i < 4) {
      val top = 28 + i
      if(result.getOccupant(top) == DARKMAN) result = result.updated(top, DARKKING)
      if(result.getOccupant(i) == LIGHTMAN) result = result.updated(i, LIGHTKING)
      i += 1
    }
    result
  }


}