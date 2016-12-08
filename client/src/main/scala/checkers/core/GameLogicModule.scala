package checkers.core

import checkers.computer.{DefaultEvaluator, Evaluator, Searcher, ShufflerFactory}
import checkers.core.tables.TablesModule
import com.softwaremill.macwire._

trait GameLogicModule {
  def rulesSettings: RulesSettings

  def boardInitializer: BoardInitializer

  def drawLogic: DrawLogic

  def moveExecutor: MoveExecutor

  def moveGenerator: MoveGenerator

  def moveTreeFactory: MoveTreeFactory

  def animationPlanner: AnimationPlanner

  def searcher: Searcher

  def evaluator: Evaluator

  def shufflerFactory: ShufflerFactory
}

class GameLogicModuleFactory(tablesModule: TablesModule,
                             shufflerFactory: ShufflerFactory,
                             boardInitializer: BoardInitializer,
                             animationSettings: AnimationSettings) extends (RulesSettings => GameLogicModule) {

  def apply(rulesSettings: RulesSettings): GameLogicModule = {
    val mySettings = rulesSettings
    val myShufflerFactory = shufflerFactory
    val myBoardInitializer = boardInitializer
    new GameLogicModule {
      import tablesModule._

      val rulesSettings: RulesSettings = mySettings

      val animSettings: AnimationSettings = animationSettings

      val shufflerFactory: ShufflerFactory = myShufflerFactory

      val boardInitializer: BoardInitializer = myBoardInitializer

      lazy val drawLogic: DrawLogic = wire[DefaultDrawLogic]

      lazy val moveExecutor: MoveExecutor = wire[MoveExecutor]

      lazy val moveGenerator: MoveGenerator = wire[MoveGenerator]

      lazy val moveTreeFactory: MoveTreeFactory = wire[MoveTreeFactory]

      lazy val animationPlanner: AnimationPlanner = wire[AnimationPlanner]

      lazy val evaluator: DefaultEvaluator = wire[DefaultEvaluator]

      lazy val searcher: Searcher = wire[Searcher]

    }
  }

}


