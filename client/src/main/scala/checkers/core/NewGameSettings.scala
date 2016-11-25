package checkers.core

import checkers.computer.DefaultPrograms

case class NewGameSettings(rulesSettings: RulesSettings,
                           darkProgramId: Option[String],
                           lightProgramId: Option[String])


object NewGameSettings {
  val standardHumanHuman = NewGameSettings(RulesSettings.default,
    None, None)

  val standardHumanTrivialPlayer =  NewGameSettings(RulesSettings.default,
    None,
    Some(DefaultPrograms.ids.TrivialPlayer))

  val standardTrivialPlayers =  NewGameSettings(RulesSettings.default,
    Some(DefaultPrograms.ids.TrivialPlayer),
    Some(DefaultPrograms.ids.TrivialPlayer))

  val default: NewGameSettings = standardHumanTrivialPlayer
}