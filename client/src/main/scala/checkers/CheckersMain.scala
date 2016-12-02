package checkers

import checkers.logger._
import checkers.modules.CoreModule
import checkers.style.GlobalStyles
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
    val module = new CoreModule { }
    val application = module.application
    application.start(host, dialogHost)
  }

}
