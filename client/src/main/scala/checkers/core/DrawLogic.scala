package checkers.core

class DrawLogic(rulesSettings: RulesSettings) {
  def canProposeDraw(gameState: GameState): Option[Int] = {
    None
  }

  def canAcceptDraw(gameState: GameState): Boolean = {
    true
  }
}