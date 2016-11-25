package checkers.core

import checkers.computer.ProgramRegistry
import org.scalajs.dom

class GameFactory(programRegistry: ProgramRegistry,
                  gameLogicModuleFactory: GameLogicModuleFactory,
                  screenLayoutSettingsProvider: ScreenLayoutSettingsProvider) {

  def create(settings: NewGameSettings, host: dom.Node): Game = {
    val darkEntry = for {
      id <- settings.darkProgramId
      entry <- programRegistry.findEntry(id)
    } yield entry

    val lightEntry = for {
      id <- settings.lightProgramId
      entry <- programRegistry.findEntry(id)
    } yield entry

    val gameLogicModule = gameLogicModuleFactory.apply(settings.rulesSettings)

    val darkComputer = for {
      entry <- darkEntry
    } yield entry.makeComputerPlayer(gameLogicModule)

    val lightComputer = for {
      entry <- lightEntry
    } yield entry.makeComputerPlayer(gameLogicModule)

    val darkPlayer = darkComputer.getOrElse(Human)
    val lightPlayer = lightComputer.getOrElse(Human)

    val gameConfig = GameConfig(settings.rulesSettings, PlayerConfig(darkPlayer, lightPlayer))
    createGame(gameLogicModule, gameConfig, host)
  }

//  // Human vs. TrivialPlayer
//  def createSimple1(host: dom.Node) = create(NewGameSettings.standardHumanTrivialPlayer, host)
//
//  // Human vs. Human
//  def createSimple2(host: dom.Node) = create(NewGameSettings.standardHumanHuman, host)
//
//  // Trivial Player vs. Trivial Player
//  def createSimple3(host: dom.Node) = create(NewGameSettings.standardTrivialPlayers, host)

  private def createGame(gameLogicModule: GameLogicModule, gameConfig: GameConfig, host: dom.Node): Game = {
    val driver = new GameDriver(gameLogicModule)(gameConfig.playerConfig)
    new Game(driver, screenLayoutSettingsProvider)(host)
  }

}