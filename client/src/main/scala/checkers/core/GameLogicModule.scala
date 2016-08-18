package checkers.core

import checkers.core.tables.TablesModule
import com.softwaremill.macwire._

trait GameLogicModule {
  def rulesSettings: RulesSettings

  def drawLogic: DrawLogic

  def moveExecutor: MoveExecutor

  def moveGenerator: MoveGenerator

  def moveTreeFactory: MoveTreeFactory

  def animationPlanner: AnimationPlanner
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
    }
  }

}


