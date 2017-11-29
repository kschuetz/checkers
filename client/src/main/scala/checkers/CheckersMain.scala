package checkers

import checkers.logger._
import checkers.modules.{BasicsModule, CoreModule, UserInterfaceModule}
import checkers.style.GlobalStyles
import org.scalajs.dom
import org.scalajs.dom.UIEvent

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._

@JSExportTopLevel("CheckersMain")
object CheckersMain {

  @JSExport
  def main(args: Array[String]): Unit = {
    log.warn("Application starting")

    // create stylesheet
    GlobalStyles.addToDocument()

    val host = dom.document.getElementById("root")
    val dialogHost = dom.document.getElementById("dialog-root")
    bootstrap(host, dialogHost)
  }

  private def bootstrap(host: dom.Element, dialogHost: dom.Element): Unit = {
    val module = new BasicsModule with CoreModule with UserInterfaceModule { }
    val application = module.application

    val session = application.start(host, dialogHost)

    dom.window.onresize = { _: UIEvent =>
      session.windowResized()
    }
  }

}
