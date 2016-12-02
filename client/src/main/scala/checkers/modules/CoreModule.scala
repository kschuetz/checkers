package checkers.modules

import checkers.computer.{DefaultPrograms, DefaultShufflerFactory, ProgramRegistry, ShufflerFactory}
import checkers.core._
import checkers.core.tables.TablesModule
import checkers.persistence.{LocalStorageNewGameSettingsPersister, NewGameSettingsPersister}
import checkers.userinterface.GameScreen
import com.softwaremill.macwire.wire

trait CoreModule {
  protected def gameScreen: GameScreen

  lazy val programRegistry: ProgramRegistry = {
    val result = new ProgramRegistry
    DefaultPrograms.registerAll(result)
    result
  }

  lazy val screenLayoutSettingsProvider: ScreenLayoutSettingsProvider = ConstantScreenLayoutSettings(DefaultScreenLayoutSettings)

  lazy val applicationSettingsProvider: ApplicationSettingsProvider = DefaultApplicationSettingsProvider

  lazy val animationSettings: AnimationSettings = DefaultAnimationSettings

  lazy val tablesModule: TablesModule = wire[TablesModule]

  lazy val boardInitializer: BoardInitializer = DefaultBoardInitializer
  //    lazy val boardInitializer: BoardInitializer = new InitializerFromBoard(
  //      BoardUtils.parseBoard(
  //        """
  //            - - - -
  //           - - d -
  //            - - - -
  //           l - - -
  //            d - - -
  //           - - - -
  //            - d - -
  //           - - - -
  //        """)
  //    )

  lazy val shufflerFactory: ShufflerFactory = wire[DefaultShufflerFactory]

  lazy val scheduler: Scheduler = wire[DefaultScheduler]

  lazy val makeGameLogicModule: GameLogicModuleFactory = wire[GameLogicModuleFactory]

  lazy val gameFactory: GameFactory = wire[GameFactory]

  lazy val newGameSettingsPersister: NewGameSettingsPersister = LocalStorageNewGameSettingsPersister

  lazy val application: Application = wire[Application]
}