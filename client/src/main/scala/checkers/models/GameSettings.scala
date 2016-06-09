package checkers.models

import checkers.core.RulesSettings

case class GameSettings(rules: RulesSettings)


object GameSettings {
  val default = GameSettings(
    rules = RulesSettings.default
  )
}