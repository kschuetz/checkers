package checkers.core

import checkers.computer._
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

  def moveSelectionMethodChooser: MoveSelectionMethodChooser
}

trait DefaultGameLogicModule extends GameLogicModule {
  lazy val tablesModule: TablesModule = wire[TablesModule]

  import tablesModule._

  lazy val rulesSettings: RulesSettings = RulesSettings.default

  lazy val animSettings: AnimationSettings = wire[DefaultAnimationSettings]

  lazy val shufflerFactory: ShufflerFactory = wire[DefaultShufflerFactory]

  lazy val boardInitializer: BoardInitializer = DefaultBoardInitializer

  lazy val drawLogic: DrawLogic = wire[DefaultDrawLogic]

  lazy val moveExecutor: MoveExecutor = wire[MoveExecutor]

  lazy val moveGenerator: MoveGenerator = wire[MoveGenerator]

  lazy val moveTreeFactory: MoveTreeFactory = wire[MoveTreeFactory]

  lazy val animationPlanner: AnimationPlanner = wire[AnimationPlanner]

  lazy val evaluator: DefaultEvaluator = wire[DefaultEvaluator]

  lazy val searcher: Searcher = wire[Searcher]

  lazy val moveSelectionMethodChooser: MoveSelectionMethodChooser = DefaultMoveSelectionMethodChooser
}

class GameLogicModuleFactory(tablesModule: TablesModule,
                             shufflerFactory: ShufflerFactory,
                             boardInitializer: BoardInitializer,
                             moveSelectionMethodChooser: MoveSelectionMethodChooser,
                             animationSettings: AnimationSettings) extends (RulesSettings => GameLogicModule) {

  def apply(rulesSettings: RulesSettings): GameLogicModule = {
    val myTablesModule = tablesModule
    val mySettings = rulesSettings
    val myShufflerFactory = shufflerFactory
    val myBoardInitializer = boardInitializer
    val myMoveSelectionMethodChooser = moveSelectionMethodChooser

    new DefaultGameLogicModule {
      override lazy val tablesModule: TablesModule = myTablesModule

      override lazy val rulesSettings: RulesSettings = mySettings

      override lazy val animSettings: AnimationSettings = animationSettings

      override lazy val shufflerFactory: ShufflerFactory = myShufflerFactory

      override lazy val boardInitializer: BoardInitializer = myBoardInitializer

      override lazy val moveSelectionMethodChooser: MoveSelectionMethodChooser = myMoveSelectionMethodChooser
    }
  }

}


