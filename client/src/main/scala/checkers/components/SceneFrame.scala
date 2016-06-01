package checkers.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactAttr
import japgolly.scalajs.react.vdom.svg.prefix_<^._

object SceneFrame {
  case class Properties()

  val Backdrop = ReactComponentB[Unit]("Backdrop")
    .render_P { _ =>
      <.rect(
        ReactAttr.ClassName := "backdrop",
        ^.x := 0,
        ^.y := 0,
        ^.width := 100000,
        ^.height := 100000
      )
    }.build

  val component = ReactComponentB[Properties]("SceneFrame")
    .render_P { props =>
      <.g(
        Backdrop(),
        PlayField()
      )
    }.build

  def apply(props: Properties) = component(props)

}