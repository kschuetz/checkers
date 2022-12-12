package checkers.test.generators

import checkers.consts._
import checkers.core.MoveExecutorTests._
import checkers.core.{BoardStack, MoveDecoder, MoveGenerator, MoveList}
import nyaya.gen.Gen

trait BoardWithMovesGenerators extends BoardGenerators with SideGenerator {

  protected def moveDecoder: MoveDecoder
  protected def moveGenerator: MoveGenerator

  /**
    * A board with all of its legal moves for the given player
    */
  case class BoardWithMoves(board: BoardStack,
                            turnToMove: Side,
                            legalMoves: MoveList)

  /**
    * A board and a legal move for the given player
    */
  case class BoardWithMove(board: BoardStack,
                           turnToMove: Side,
                           legalMove: Option[List[Int]]) {
    def hasMove: Boolean = legalMove.nonEmpty
  }


  protected lazy val genBoardWithMoves: Gen[BoardWithMoves] = for {
    turnToMove <- genSide
    boardState <- genBoard
    boardStack = BoardStack.fromBoard(boardState)
  } yield BoardWithMoves(boardStack, turnToMove, moveGenerator.generateMoves(boardStack, turnToMove))

  protected lazy val genBoardWithMove: Gen[BoardWithMove] = genBoardWithMoves.flatMap {
    case BoardWithMoves(board, turnToMove, legalMoves) =>
      if(legalMoves.isEmpty) Gen.pure(BoardWithMove(board, turnToMove, None))
      else Gen.chooseInt(legalMoves.count).map { moveIndex =>
        moveDecoder.load(legalMoves, moveIndex)
        val move = moveDecoder.pathToList
        BoardWithMove(board, turnToMove, Some(move))
      }
  }

}