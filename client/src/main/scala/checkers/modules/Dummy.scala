package checkers.modules

import checkers.shell.Location
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._

object Dummy {

  private val component = ReactComponentB[Location]("Dummy")
    .render_P { loc =>
      <.div(
        <.h2("Dummy Module"),
        <.h2(loc.toString)
      )
    }.build

  def apply(loc: Location, router: RouterCtl[Location]) =
    component(loc)
}