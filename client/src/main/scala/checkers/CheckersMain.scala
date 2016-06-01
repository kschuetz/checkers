package checkers

import checkers.logger._
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

    ReactDOM.render(Sandbox.test, dom.document.getElementById("root"))
  }
}
