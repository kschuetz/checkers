package checkers.models

import checkers.game.RulesSettings

case class GameSettings(rules: RulesSettings)


object GameSettings {
  val default = GameSettings(
    rules = RulesSettings.default
  )
}