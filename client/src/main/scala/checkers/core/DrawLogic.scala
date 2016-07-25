package checkers.core

class DrawLogic(rulesSettings: RulesSettings) {
  def canProposeDraw[DS, LS](gameState: GameState[DS, LS]): Option[Int] = {
    None
  }

  def canAcceptDraw[DS, LS](gameState: GameState[DS, LS]): Boolean = {
    true
  }
}