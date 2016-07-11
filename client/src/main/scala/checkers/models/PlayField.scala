package checkers.models

import checkers.core.OldGameState

case class PlayField(gameState: OldGameState,
                     orientation: BoardOrientation,
                     ghostPiece: Option[GhostPiece],
                     clickableSquares: Set[Int],
                     highlightedSquares: Set[Int],
                     animations: List[Animation]) {
  def hasActiveAnimations(nowTime: Double): Boolean =
    animations.exists(anim => !anim.isExpired(nowTime))
}

