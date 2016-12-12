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

  def create(settings: NewGameSettings, initialSeeds: InitialSeeds, host: dom.Node): Game = {
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
    } yield entry.makeComputerPlayer(gameLogicModule, initialSeeds.darkPlayer)

    val lightComputer = for {
      entry <- lightEntry
    } yield entry.makeComputerPlayer(gameLogicModule, initialSeeds.lightPlayer)

    val darkPlayer = darkComputer.getOrElse(Human)
    val lightPlayer = lightComputer.getOrElse(Human)

    val darkMentor = createMentor(gameLogicModule, darkPlayer, initialSeeds.darkMentor)
    val lightMentor = createMentor(gameLogicModule, lightPlayer, initialSeeds.lightMentor)

    val mentorConfig = MentorConfig(darkMentor, lightMentor)

    val gameConfig = GameConfig(settings.rulesSettings, PlayerConfig(darkPlayer, lightPlayer), mentorConfig)
    createGame(gameLogicModule, gameConfig, host)
  }


  private def createMentor(gameLogicModule: GameLogicModule, player: Player, initialSeed: Option[Long]): Option[Program] = {
    if(player.isComputer) None
    else {
      Option(mentorFactory.makeProgram(gameLogicModule, initialSeed))
    }
  }

  private def createGame(gameLogicModule: GameLogicModule, gameConfig: GameConfig, host: dom.Node): Game = {
    val driver = new GameDriver(gameLogicModule)(gameConfig.playerConfig, gameConfig.mentorConfig)
    new Game(driver, scheduler, applicationSettingsProvider, screenLayoutSettingsProvider, gameScreen)(host)
  }

}