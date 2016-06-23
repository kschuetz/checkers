package checkers.test

import checkers.core.{GameLogicModuleFactory, RulesSettings}
import checkers.core.tables.TablesModule
import com.softwaremill.macwire._


trait DefaultGameLogicTestModule {
  lazy val rulesSettings = RulesSettings.default

  lazy val tablesModule = wire[TablesModule]

  lazy val gameLogicModuleFactory: GameLogicModuleFactory = wire[GameLogicModuleFactory]

  lazy val gameLogicModule = gameLogicModuleFactory.apply(rulesSettings)
}