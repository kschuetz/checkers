package checkers.core

import checkers.computer.{MentorFactory, Program, ProgramRegistry}
import checkers.userinterface.GameScreen
import org.scalajs.dom

class GameFactory(programRegistry: ProgramRegistry,
                  gameLogicModuleFactory: GameLogicModuleFactory,
                  mentorFactory: MentorFactory,
                  scheduler: Scheduler,
                  applicationSettingsProvider: ApplicationSettingsProvider,
                  screenLayoutSettingsProvider: ScreenLayoutSettingsProvider,
                  gameScreen: GameScreen) {

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

    val darkMentor = createMentor(gameLogicModule, darkPlayer)
    val lightMentor = createMentor(gameLogicModule, lightPlayer)

    val mentorConfig = MentorConfig(darkMentor, lightMentor)

    val gameConfig = GameConfig(settings.rulesSettings, PlayerConfig(darkPlayer, lightPlayer), mentorConfig)
    createGame(gameLogicModule, gameConfig, host)
  }


  private def createMentor(gameLogicModule: GameLogicModule, player: Player): Option[Program] = {
    if(player.isComputer) None
    else {
      Option(mentorFactory.makeProgram(gameLogicModule))
    }
  }

  private def createGame(gameLogicModule: GameLogicModule, gameConfig: GameConfig, host: dom.Node): Game = {
    val driver = new GameDriver(gameLogicModule)(gameConfig.playerConfig)
    new Game(driver, scheduler, applicationSettingsProvider, screenLayoutSettingsProvider, gameScreen)(host)
  }

}