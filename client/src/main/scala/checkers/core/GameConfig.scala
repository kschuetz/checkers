package checkers.core

import checkers.consts._

case class PlayerConfig(darkPlayer: Player, lightPlayer: Player) {
  def getPlayer(side: Side): Player = if(side == DARK) darkPlayer else lightPlayer
}

case class GameConfig(rulesSettings: RulesSettings, playerConfig: PlayerConfig)

object GameConfig {
  val test1 = GameConfig(RulesSettings.default, PlayerConfig(Human, Human))
}