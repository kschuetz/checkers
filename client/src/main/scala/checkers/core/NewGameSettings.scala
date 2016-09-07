package checkers.core

import checkers.computer.DefaultPrograms

case class NewGameSettings(rulesSettings: RulesSettings,
                           darkProgramId: Option[String],
                           lightProgramId: Option[String])


object NewGameSettings {
  val default = NewGameSettings(RulesSettings.default,
    None,
    Some(DefaultPrograms.ids.TrivialPlayer))
}