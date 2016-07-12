package checkers

import checkers.benchmark.MoveGeneratorBenchmarks
import checkers.core.tables.TablesModule
import checkers.core.{GameConfig, GameLogicModuleFactory, RulesSettings}
import checkers.driver.GameScreenDriver
import checkers.logger._
import checkers.models.GameScreenModel
import checkers.style.GlobalStyles
import checkers.util.DebugUtils
import com.softwaremill.macwire._
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._

@JSExport("CheckersMain")
object CheckersMain extends js.JSApp {

  @JSExport
  def main(): Unit = {
    log.warn("Application starting")

    MoveGeneratorBenchmarks.test1()

    // create stylesheet
    GlobalStyles.addToDocument()

    val host = dom.document.getElementById("root")
    sandbox1(host)
  }

  private def sandbox1(host: dom.Node): Unit = {
    lazy val rulesSettings = RulesSettings.default

    lazy val tablesModule = wire[TablesModule]

    lazy val gameLogicModuleFactory: GameLogicModuleFactory = wire[GameLogicModuleFactory]

    lazy val gameLogicModule = gameLogicModuleFactory.apply(rulesSettings)

    val config = GameConfig.createSimple1(rulesSettings, gameLogicModule.moveGenerator)

    val model = GameScreenModel.initial(config)
    println(model.board.data)
    DebugUtils.printOccupants(model.board)

    val driver = new GameScreenDriver(host, model)
    driver.run()
  }
}
