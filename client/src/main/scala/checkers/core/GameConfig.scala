package checkers.core

import checkers.computer.TrivialPlayer

case class PlayerConfig[DS, LS](darkPlayer: Player[DS], lightPlayer: Player[LS])

case class GameConfig[DS, LS](rulesSettings: RulesSettings, playerConfig: PlayerConfig[DS, LS])

object GameConfig {
  val test1 = GameConfig(RulesSettings.default, PlayerConfig(Human, Human))

//  def createSimple1(rulesSettings: RulesSettings, moveGenerator: MoveGenerator) = {
//    val light = Computer(new TrivialPlayer(moveGenerator)(None))
//    val dark = Human
//    GameConfig(rulesSettings, dark, light)
//  }

}