package checkers.core

import checkers.computer.{ProgramRegistry, TrivialPlayer}
import org.scalajs.dom

class GameFactory(programRegistry: ProgramRegistry,
                  gameLogicModuleFactory: GameLogicModuleFactory,
                  screenLayoutSettingsProvider: ScreenLayoutSettingsProvider) {

  def create(rulesSettings: RulesSettings, darkProgramId: Option[String], lightProgramId: Option[String], host: dom.Node) = {
    val darkEntry = for {
      id <- darkProgramId
      entry <- programRegistry.findEntry(id)
    } yield entry

    val lightEntry = for {
      id <- lightProgramId
      entry <- programRegistry.findEntry(id)
    } yield entry

    val gameLogicModule = gameLogicModuleFactory.apply(rulesSettings)

    val darkComputer = for {
      entry <- darkEntry
    } yield entry.makeComputerPlayer(gameLogicModule)

    val lightComputer = for {
      entry <- lightEntry
    } yield entry.makeComputerPlayer(gameLogicModule)

    val darkPlayer = darkComputer.getOrElse(Human)
    val lightPlayer = lightComputer.getOrElse(Human)

    val gameConfig = GameConfig(rulesSettings, PlayerConfig(darkPlayer, lightPlayer))
    createGame(gameLogicModule, gameConfig, host)
  }

  // Human vs. TrivialPlayer
  def createSimple1(host: dom.Node) = {
    val rulesSettings = RulesSettings.default
    create(rulesSettings, None, Some("TrivialPlayer"), host)
  }

  // Human vs. Human
  def createSimple2(host: dom.Node) = {
    val rulesSettings = RulesSettings.default
    create(rulesSettings, None, None, host)
  }

  // Trivial Player vs. Trivial Player
  def createSimple3(host: dom.Node) = {
    val rulesSettings = RulesSettings.default
    create(rulesSettings, Some("TrivialPlayer"), Some("TrivialPlayer"), host)
  }

  private def createGame(gameLogicModule: GameLogicModule, gameConfig: GameConfig, host: dom.Node): Game = {
    val driver = new GameDriver(gameLogicModule)(gameConfig.playerConfig)
    new Game(driver, screenLayoutSettingsProvider)(host)
  }

}