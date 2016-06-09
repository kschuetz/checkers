package checkers.models

import checkers.core.GameState

case class PlayField(gameState: GameState,
                     orientation: BoardOrientation,
                     ghostPiece: Option[GhostPiece],
                     clickableSquares: Set[Int],
                     highlightedSquares: Set[Int],
                     animations: List[Animation]) {
  def hasActiveAnimations(nowTime: Double): Boolean =
    animations.exists(anim => !anim.isExpired(nowTime))
}

