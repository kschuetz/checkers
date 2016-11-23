package checkers.test

import checkers.computer.{DefaultShufflerFactory, ShufflerFactory}
import checkers.core.tables.TablesModule
import checkers.core.{AnimationSettings, DefaultAnimationSettings, GameConfig, GameLogicModuleFactory}
import com.softwaremill.macwire._


trait DefaultGameLogicTestModule {
  lazy val gameConfig = GameConfig.test1

  lazy val rulesSettings = gameConfig.rulesSettings

  lazy val tablesModule = wire[TablesModule]

  lazy val animationSettings: AnimationSettings = DefaultAnimationSettings

  lazy val gameLogicModuleFactory: GameLogicModuleFactory = wire[GameLogicModuleFactory]

  lazy val shufflerFactory: ShufflerFactory = new DefaultShufflerFactory

  lazy val gameLogicModule = gameLogicModuleFactory.apply(rulesSettings)
}