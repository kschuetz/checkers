package checkers.core

import checkers.computer.TrivialPlayer

case class PlayerConfig(darkPlayer: Player, lightPlayer: Player)

case class GameConfig(rulesSettings: RulesSettings, playerConfig: PlayerConfig)

object GameConfig {
  val test1 = GameConfig(RulesSettings.default, PlayerConfig(Human, Human))

//  def createSimple1(rulesSettings: RulesSettings, moveGenerator: MoveGenerator) = {
//    val light = Computer(new TrivialPlayer(moveGenerator)(None))
//    val dark = Human
//    GameConfig(rulesSettings, dark, light)
//  }

}