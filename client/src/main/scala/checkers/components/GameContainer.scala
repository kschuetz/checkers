package checkers.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^.{< => _, ^ => _}
import japgolly.scalajs.react.vdom.svg.prefix_<^._

object GameContainer {
  val component = ReactComponentB[Unit]("GameContainer")
    .render_P { _ =>
      <.svg(
        ^.id := "game-container",
        ^.width := "800px",
        ^.height := "800px",
        SceneFrame(SceneFrame.Properties())
      )
    }.build

  def apply() = component()
}