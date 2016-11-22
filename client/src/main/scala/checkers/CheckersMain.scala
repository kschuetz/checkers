package checkers

import checkers.computer.{DefaultPrograms, NoShuffleFactory, ProgramRegistry, ShufflerFactory}
import checkers.core.tables.TablesModule
import checkers.core.{GameFactory, _}
import checkers.logger._
import checkers.persistence.{LocalStorageNewGameSettingsPersister, NewGameSettingsPersister}
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

    // create stylesheet
    GlobalStyles.addToDocument()

    val host = dom.document.getElementById("root")
    val dialogHost = dom.document.getElementById("dialog-root")
    bootstrap(host, dialogHost)
  }

  private def bootstrap(host: dom.Node, dialogHost: dom.Node): Unit = {
    lazy val programRegistry = {
      val result = new ProgramRegistry
      DefaultPrograms.registerAll(result)
      result
    }

    lazy val screenLayoutSettingsProvider: ScreenLayoutSettingsProvider = ConstantScreenLayoutSettings(DefaultScreenLayoutSettings)

    lazy val animationSettings: AnimationSettings = DefaultAnimationSettings

    lazy val tablesModule = wire[TablesModule]

    lazy val shufflerFactory: ShufflerFactory = NoShuffleFactory

    lazy val makeGameLogicModule: GameLogicModuleFactory = wire[GameLogicModuleFactory]

    lazy val gameFactory: GameFactory = wire[GameFactory]

    lazy val newGameSettingsPersister: NewGameSettingsPersister = LocalStorageNewGameSettingsPersister

    lazy val application: Application = wire[Application]

    application.start(host, dialogHost)
  }

}
