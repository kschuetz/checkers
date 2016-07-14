package checkers.core

import checkers.core.tables.TablesModule
import com.softwaremill.macwire._

trait GameLogicModule {
  def moveExecutor: MoveExecutor

  def moveGenerator: MoveGenerator

  def moveTreeFactory: MoveTreeFactory

  def turnEvaluator: TurnEvaluator
}

class GameLogicModuleFactory(tablesModule: TablesModule) extends (RulesSettings => GameLogicModule) {

  def apply(rulesSettings: RulesSettings): GameLogicModule = {
    val mySettings = rulesSettings
    new GameLogicModule {
      import tablesModule._

      val rulesSettings = mySettings

      lazy val moveExecutor = wire[MoveExecutor]

      lazy val moveGenerator = wire[MoveGenerator]

      lazy val moveTreeFactory = wire[MoveTreeFactory]

      lazy val turnEvaluator = wire[TurnEvaluator]
    }
  }

}


