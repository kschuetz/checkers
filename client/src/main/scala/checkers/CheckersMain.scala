package checkers

import checkers.benchmark.MoveGeneratorBenchmarks
import checkers.computer.{DefaultPrograms, ProgramRegistry}
import checkers.core.tables.TablesModule
import checkers.core.{GameFactory, _}
import checkers.logger._
import checkers.persistence.{LocalStorageNewGameSettingsPersister, NewGameSettingsPersister, NullNewGameSettingsPersister}
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
    lazy val programRegistry = {
      val result = new ProgramRegistry
      DefaultPrograms.registerAll(result)
      result
    }

    lazy val screenLayoutSettingsProvider: ScreenLayoutSettingsProvider = ConstantScreenLayoutSettings(DefaultScreenLayoutSettings)

    lazy val rulesSettings = RulesSettings.default

    lazy val animationSettings: AnimationSettings = DefaultAnimationSettings

    lazy val tablesModule = wire[TablesModule]

    lazy val drawLogic = wire[DrawLogic]

    lazy val makeGameLogicModule: GameLogicModuleFactory = wire[GameLogicModuleFactory]

    lazy val gameFactory: GameFactory = wire[GameFactory]

    lazy val newGameSettingsPersister: NewGameSettingsPersister = NullNewGameSettingsPersister

    lazy val mainLoop: MainLoop = wire[MainLoop]

    mainLoop.start(host, host)
//    val game = gameFactory.createSimple1(host)
//    game.run()
  }

}
