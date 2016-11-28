package checkers.test

import checkers.computer.{DefaultShufflerFactory, ShufflerFactory}
import checkers.core.tables.TablesModule
import checkers.core._
import com.softwaremill.macwire._


trait DefaultGameLogicTestModule {
  lazy val gameConfig: GameConfig = GameConfig.test1

  lazy val rulesSettings: RulesSettings = gameConfig.rulesSettings

  lazy val tablesModule: TablesModule = wire[TablesModule]

  lazy val animationSettings: AnimationSettings = DefaultAnimationSettings

  lazy val gameLogicModuleFactory: GameLogicModuleFactory = wire[GameLogicModuleFactory]

  lazy val shufflerFactory: ShufflerFactory = new DefaultShufflerFactory

  lazy val boardInitializer: BoardInitializer = DefaultBoardInitializer

  lazy val gameLogicModule: GameLogicModule = gameLogicModuleFactory.apply(rulesSettings)
}