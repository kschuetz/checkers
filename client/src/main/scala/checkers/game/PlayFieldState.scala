package checkers.game

import checkers.game.SceneObject.GhostPiece


case class PlayFieldState(gameState: GameState,
                          orientation: BoardOrientation,
                          ghostPiece: Option[GhostPiece],
                          highlightedSquares: Set[Int],
                          animations: List[Animation]) {
  def hasActiveAnimations(nowTime: Double): Boolean =
    animations.exists(anim => !anim.isExpired(nowTime))
}