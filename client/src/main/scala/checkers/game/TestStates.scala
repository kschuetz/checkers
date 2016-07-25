package checkers.game

import checkers.consts._

object TestStates {
  val board1 = Vector(
    LIGHTKING, LIGHTKING, LIGHTKING, LIGHTKING,
    LIGHTMAN, LIGHTMAN, LIGHTMAN, LIGHTMAN,
    LIGHTMAN, LIGHTMAN, LIGHTMAN, LIGHTMAN,
    EMPTY, EMPTY, EMPTY, EMPTY,
    EMPTY, EMPTY, EMPTY, EMPTY,
    DARKMAN, DARKMAN, DARKMAN, DARKMAN,
    DARKMAN, DARKMAN, DARKMAN, DARKMAN,
    DARKKING, DARKKING, DARKKING, DARKKING)

//  val gameState1 = OldGameState(BoardState.empty, DARK)
//
//  val playFieldState1 = PlayField(
//    gameState = gameState1,
//    orientation = BoardOrientation.Normal,
//    ghostPiece = None,
//    highlightedSquares = Set.empty,
//    clickableSquares = Set(8, 9, 10, 11, 20, 21, 22, 23),
//    animations = Nil)

  //List(Animation.MovingPiece(18, 22, 0, 0)))

  //  case class PlayFieldState(gameState: GameState,
  //                            orientation: BoardOrientation,
  //                            ghostPiece: Option[GhostPiece],
  //                            highlightedSquares: Set[Int],
  //                            animations: List[Animation])
}