package checkers.test.generators

import checkers.consts._
import checkers.core.MoveExecutorTests._
import checkers.core.{BoardStack, MoveDecoder, MoveGenerator, MoveList}
import nyaya.gen.Gen

trait BoardWithMovesGenerators extends BoardGenerators with ColorGenerator {

  protected def moveDecoder: MoveDecoder
  protected def moveGenerator: MoveGenerator

  /**
    * A board with all of its legal moves for the given player
    */
  case class BoardWithMoves(board: BoardStack,
                            turnToMove: Color,
                            legalMoves: MoveList)

  /**
    * A board and a legal move for the given player
    */
  case class BoardWithMove(board: BoardStack,
                           turnToMove: Color,
                           legalMove: List[Int])


  protected lazy val genBoardWithMoves: Gen[BoardWithMoves] = for {
    turnToMove <- genColor
    boardState <- genBoard
    boardStack = BoardStack.fromBoard(boardState)
  } yield BoardWithMoves(boardStack, turnToMove, moveGenerator.generateMoves(boardStack, turnToMove))

  protected lazy val genBoardWithMove: Gen[BoardWithMove] = genBoardWithMoves.withFilter(_.legalMoves.nonEmpty).flatMap {
    case BoardWithMoves(board, turnToMove, legalMoves) =>
      Gen.chooseInt(legalMoves.count).map { moveIndex =>
        moveDecoder.load(legalMoves, moveIndex)
        val move = moveDecoder.pathToList
        BoardWithMove(board, turnToMove, move)
      }
  }

}