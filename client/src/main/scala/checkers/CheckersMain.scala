package checkers

import checkers.logger._
import checkers.modules.Dummy
import checkers.shell.Location
import checkers.style.GlobalStyles
import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._

@JSExport("CheckersMain")
object CheckersMain extends js.JSApp {

  val dummyPage = Dummy.apply _

  // configure the router
  val routerConfig = RouterConfigDsl[Location].buildConfig { dsl =>
    import dsl._

    // wrap/connect components to the circuit
    (staticRoute("#sandbox", Location.Sandbox) ~> renderR(ctl => Dummy(Location.Sandbox, ctl))
      ).notFound(redirectToPage(Location.Sandbox)(Redirect.Replace))
  }.renderWith(layout)

  // base layout for all pages
  def layout(c: RouterCtl[Location], r: Resolution[Location]) = {
    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(^.className := "container",
          <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", "Checkers")),
          <.div(^.className := "collapse navbar-collapse",
            "Welcome to Checkers"
          )
        )
      ),
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }

  @JSExport
  def main(): Unit = {
    log.warn("Application starting")

    // create stylesheet
    GlobalStyles.addToDocument()
    // create the router
    val router = Router(BaseUrl.until_#, routerConfig)
    // tell React to render the router in the document body
    ReactDOM.render(router(), dom.document.getElementById("root"))
  }
}
