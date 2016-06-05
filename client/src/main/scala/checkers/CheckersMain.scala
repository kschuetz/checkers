package checkers

import checkers.driver.GameScreenDriver
import checkers.logger._
import checkers.models.{GameScreenModel, GameSettings}
import checkers.modules.Sandbox
import checkers.style.GlobalStyles
import japgolly.scalajs.react.ReactDOM
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
    sandbox2(host)
  }

  private def sandbox1(host: dom.Node): Unit = {
    ReactDOM.render(Sandbox.test, host)
  }

  private def sandbox2(host: dom.Node): Unit = {
    val model = GameScreenModel.initial(GameSettings.default)
    val driver = new GameScreenDriver(host, model)
    driver.run()
  }
}
