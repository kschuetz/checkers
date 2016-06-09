package checkers.game

import checkers.core._
import checkers.models.{BoardOrientation, PlayField}

object TestStates {
  val board1 = Vector(
    LightKing, LightKing, LightKing, LightKing,
    LightMan, LightMan, LightMan, LightMan,
    LightMan, LightMan, LightMan, LightMan,
    Empty, Empty, Empty, Empty,
    Empty, Empty, Empty, Empty,
    DarkMan, DarkMan, DarkMan, DarkMan,
    DarkMan, DarkMan, DarkMan, DarkMan,
    DarkKing, DarkKing, DarkKing, DarkKing)

  val gameState1 = GameState(BoardState.empty, Dark)

  val playFieldState1 = PlayField(
    gameState = gameState1,
    orientation = BoardOrientation.Normal,
    ghostPiece = None,
    highlightedSquares = Set.empty,
    clickableSquares = Set(8, 9, 10, 11, 20, 21, 22, 23),
    animations = Nil)

  //List(Animation.MovingPiece(18, 22, 0, 0)))

  //  case class PlayFieldState(gameState: GameState,
  //                            orientation: BoardOrientation,
  //                            ghostPiece: Option[GhostPiece],
  //                            highlightedSquares: Set[Int],
  //                            animations: List[Animation])
}