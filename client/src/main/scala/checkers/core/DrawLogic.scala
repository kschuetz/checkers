package checkers.core

class DrawLogic {
  def canProposeDraw[DS, LS](gameState: GameState[DS, LS]): Option[Int] = {
    None
  }

  def canAcceptDraw[DS, LS](gameState: GameState[DS, LS]): Boolean = {
    true
  }
}