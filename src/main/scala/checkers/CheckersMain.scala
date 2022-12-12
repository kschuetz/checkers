package checkers

import checkers.logger._
import checkers.modules.{BasicsModule, CoreModule, UserInterfaceModule}
import checkers.style.GlobalStyles
import org.scalajs.dom
import org.scalajs.dom.{Event, UIEvent}
import scalacss.ProdDefaults._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("CheckersMain")
object CheckersMain {

  @JSExport
  def main(args: Array[String]): Unit = {
    // create stylesheet
    GlobalStyles.addToDocument()

    dom.document.addEventListener[Event]("DOMContentLoaded", (event: Event) => {
      log.info("Application starting")
      val host = dom.document.getElementById("root")
      val dialogHost = dom.document.getElementById("dialog-root")
      bootstrap(host, dialogHost)
    })
  }

  private def bootstrap(host: dom.Element, dialogHost: dom.Element): Unit = {
    val module = new BasicsModule with CoreModule with UserInterfaceModule {}
    val application = module.application

    val session = application.start(host, dialogHost)

    dom.window.onresize = { _: UIEvent =>
      session.windowResized()
    }
  }

}
