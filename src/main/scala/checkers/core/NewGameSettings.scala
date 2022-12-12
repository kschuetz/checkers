package checkers.core

import upickle.default._
import checkers.computer.DefaultPrograms

case class NewGameSettings(rulesSettings: RulesSettings,
                           darkProgramId: Option[String],
                           lightProgramId: Option[String])


object NewGameSettings {
  val standardHumanHuman = NewGameSettings(RulesSettings.default,
    None, None)

  val standardHumanTrivialPlayer =  NewGameSettings(RulesSettings.default,
    None,
    Some(DefaultPrograms.ids.DefaultComputerPlayer))

  val standardTrivialPlayers =  NewGameSettings(RulesSettings.default,
    Some(DefaultPrograms.ids.DefaultComputerPlayer),
    Some(DefaultPrograms.ids.DefaultComputerPlayer))

  val default: NewGameSettings = standardHumanTrivialPlayer

  implicit val rw: ReadWriter[NewGameSettings] = macroRW
}
