package checkers.modules

import checkers.computer.{DefaultPrograms, DefaultShufflerFactory, ProgramRegistry, ShufflerFactory}
import checkers.core._
import checkers.core.tables.TablesModule
import checkers.persistence.{LocalStorageNewGameSettingsPersister, NewGameSettingsPersister}
import checkers.userinterface.GameScreen
import checkers.userinterface.dialog.NewGameDialog
import com.softwaremill.macwire.wire

trait CoreModule {
  protected def gameScreen: GameScreen

  protected def newGameDialog: NewGameDialog

  lazy val programRegistry: ProgramRegistry = {
    val result = new ProgramRegistry
    DefaultPrograms.registerAll(result)
    result
  }

  lazy val screenLayoutSettingsProvider: ScreenLayoutSettingsProvider = wire[ScreenLayoutAdapter]

//    ConstantScreenLayoutSettings(DefaultScreenLayoutSettings)

  lazy val applicationSettingsProvider: ApplicationSettingsProvider = DefaultApplicationSettingsProvider

  lazy val animationSettings: AnimationSettings = new DefaultAnimationSettings { }

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