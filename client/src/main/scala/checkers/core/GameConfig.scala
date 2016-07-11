package checkers.core

import checkers.computer.TrivialPlayer

case class GameConfig[DS, LS](rulesSettings: RulesSettings, darkPlayer: Player[DS], lightPlayer: Player[LS])

object GameConfig {
  val test1 = GameConfig(RulesSettings.default, Human, Human)

  def createSimple1(rulesSettings: RulesSettings, moveGenerator: MoveGenerator) = {
    val light = Computer(new TrivialPlayer(moveGenerator)(None))
    val dark = Human
    GameConfig(rulesSettings, dark, light)
  }

}