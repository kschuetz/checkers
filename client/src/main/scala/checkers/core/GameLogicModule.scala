package checkers.core

import checkers.computer.{DefaultEvaluator, Evaluator, Searcher}
import checkers.core.tables.TablesModule
import com.softwaremill.macwire._

trait GameLogicModule {
  def rulesSettings: RulesSettings

  def drawLogic: DrawLogic

  def moveExecutor: MoveExecutor

  def moveGenerator: MoveGenerator

  def moveTreeFactory: MoveTreeFactory

  def animationPlanner: AnimationPlanner

  def searcher: Searcher

  def evaluator: Evaluator
}

class GameLogicModuleFactory(tablesModule: TablesModule,
                             animationSettings: AnimationSettings) extends (RulesSettings => GameLogicModule) {

  def apply(rulesSettings: RulesSettings): GameLogicModule = {
    val mySettings = rulesSettings
    new GameLogicModule {
      import tablesModule._

      val rulesSettings = mySettings

      val animSettings = animationSettings

      lazy val drawLogic = wire[DrawLogic]

      lazy val moveExecutor = wire[MoveExecutor]

      lazy val moveGenerator = wire[MoveGenerator]

      lazy val moveTreeFactory = wire[MoveTreeFactory]

      lazy val animationPlanner = wire[AnimationPlanner]

      lazy val evaluator = wire[DefaultEvaluator]

      lazy val searcher = wire[Searcher]

    }
  }

}


