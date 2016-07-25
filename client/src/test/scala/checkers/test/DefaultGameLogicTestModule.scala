package checkers.test

import checkers.core.tables.TablesModule
import checkers.core.{GameConfig, GameLogicModuleFactory}
import com.softwaremill.macwire._


trait DefaultGameLogicTestModule {
  lazy val gameConfig = GameConfig.test1

  lazy val rulesSettings = gameConfig.rulesSettings

  lazy val tablesModule = wire[TablesModule]

  lazy val gameLogicModuleFactory: GameLogicModuleFactory = wire[GameLogicModuleFactory]

  lazy val gameLogicModule = gameLogicModuleFactory.apply(gameConfig)
}