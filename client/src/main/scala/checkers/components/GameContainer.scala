package checkers.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object GameContainer {
  val component = ReactComponentB[Unit]("GameContainer")
    .render_P { _ =>
      <.svg.svg(
        ^.id := "game-container",
        ^.svg.width := "800px",
        ^.svg.height := "800px",
        SceneFrame(SceneFrame.Properties())
      )
    }.build

  def apply() = component()
}