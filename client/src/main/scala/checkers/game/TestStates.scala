package checkers.game

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

  val gameState1 = GameState(board1)

  val playFieldState1 = PlayFieldState(gameState1,
    BoardOrientation.Normal,
    None,
    Set.empty,
    Nil)

  //List(Animation.MovingPiece(18, 22, 0, 0)))

//  case class PlayFieldState(gameState: GameState,
//                            orientation: BoardOrientation,
//                            ghostPiece: Option[GhostPiece],
//                            highlightedSquares: Set[Int],
//                            animations: List[Animation])
}