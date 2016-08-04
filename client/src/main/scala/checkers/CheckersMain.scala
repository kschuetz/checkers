package checkers

import checkers.benchmark.MoveGeneratorBenchmarks
import checkers.core.tables.TablesModule
import checkers.core.{DrawLogic, GameLogicModuleFactory, RulesSettings}
import checkers.driver.GameFactory
import checkers.logger._
import checkers.style.GlobalStyles
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

    lazy val drawLogic = wire[DrawLogic]

    lazy val makeGameLogicModule: GameLogicModuleFactory = wire[GameLogicModuleFactory]

    lazy val gameFactory: GameFactory = wire[GameFactory]

    val game = gameFactory.createSimple2(host)
    game.run()
  }

}
