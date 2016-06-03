package checkers.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactAttr
import japgolly.scalajs.react.vdom.prefix_<^._

object SceneFrame {
  case class Properties()

  val Backdrop = ReactComponentB[Unit]("Backdrop")
    .render_P { _ =>
      <.svg.rect(
        ReactAttr.ClassName := "backdrop",
        ^.svg.x := 0,
        ^.svg.y := 0,
        ^.svg.width := 100000,
        ^.svg.height := 100000
      )
    }.build

  val component = ReactComponentB[Properties]("SceneFrame")
    .render_P { props =>
      <.svg.g(
        Backdrop(),
        PlayField()
      )
    }.build

  def apply(props: Properties) = component(props)

}