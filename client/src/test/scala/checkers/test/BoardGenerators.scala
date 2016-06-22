package checkers.test

import checkers.consts._
import checkers.core.BoardState
import nyaya.gen.Gen

object BoardGenerators {
  // counts for (men, kings)
  val choosePieceCount: Gen[(Int, Int)] = for {
    totalPieces <- Gen.chooseInt(12)
    kingCount <- Gen.chooseInt(totalPieces)
  } yield (totalPieces - kingCount, kingCount)

  val boardPositions: Gen[List[Int]] =
    Gen.shuffle((0 to 31).toList)

  private def updateBoard(board: BoardState, occupants: List[(Int, Occupant)], positions: List[Int]): BoardState = {
    occupants match {
      case Nil => board
      case (count, occupant) :: rest =>
        val newBoard = board.updateMany(occupant)(positions.take(count))
        updateBoard(newBoard, rest, positions.drop(count))
    }
  }

  val genBoard: Gen[BoardState] = for {
    (lightMen, lightKings) <- choosePieceCount
    (darkMen, darkKings) <- choosePieceCount
    positions <- boardPositions
  } yield updateBoard(BoardState.empty, List(lightMen -> LIGHTMAN, darkMen -> DARKMAN, lightKings -> LIGHTKING, darkKings -> DARKKING), positions)


}