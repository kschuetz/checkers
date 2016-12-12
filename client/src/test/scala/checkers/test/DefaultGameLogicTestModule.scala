package checkers.test

import checkers.computer.{DefaultMoveSelectionMethodChooser, DefaultShufflerFactory, MoveSelectionMethodChooser, ShufflerFactory}
import checkers.core._
import checkers.core.tables.TablesModule
import com.softwaremill.macwire._


trait DefaultGameLogicTestModule {
  lazy val gameConfig: GameConfig = GameConfig.test1

  lazy val rulesSettings: RulesSettings = gameConfig.rulesSettings

  lazy val tablesModule: TablesModule = wire[TablesModule]

  lazy val animationSettings: AnimationSettings = wire[DefaultAnimationSettings]

  lazy val gameLogicModuleFactory: GameLogicModuleFactory = wire[GameLogicModuleFactory]

  lazy val shufflerFactory: ShufflerFactory = new DefaultShufflerFactory

  lazy val boardInitializer: BoardInitializer = DefaultBoardInitializer

  lazy val moveSelectionMethodChooser: MoveSelectionMethodChooser = DefaultMoveSelectionMethodChooser

  lazy val gameLogicModule: GameLogicModule = gameLogicModuleFactory.apply(rulesSettings)
}