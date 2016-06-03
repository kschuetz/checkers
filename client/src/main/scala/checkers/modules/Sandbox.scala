package checkers.modules

import checkers.components.{GameContainer, SceneFrame}
import checkers.shell.Location
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._

object Sandbox {

  private val component = ReactComponentB[Location]("Sandbox")
    .render_P { loc =>
      <.div(
        ^.key := 1,
        ^.`class` := "row",
        <.div(
          ^.`class` := "col-md-12",
          GameContainer(SceneFrame.Properties())
        )
      )
    }.build

  def apply(loc: Location, router: RouterCtl[Location]) =
    component(loc)

  def test = component(Location.Sandbox)
}