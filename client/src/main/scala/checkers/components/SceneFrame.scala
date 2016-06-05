package checkers.components

import checkers.components.board.PhysicalBoard
import checkers.models.GameScreenModel
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactAttr
import japgolly.scalajs.react.vdom.prefix_<^._

object SceneFrame {

  val Backdrop = ReactComponentB[Unit]("Backdrop")
    .render_P { _ =>
      <.svg.rect(
        ReactAttr.ClassName := "backdrop",
        ^.svg.x := 0,
        ^.svg.y := 0,
        ^.svg.width := 800,
        ^.svg.height := 800
      )
    }.build

  val component = ReactComponentB[GameScreenModel]("SceneFrame")
    .render_P { model =>
      val physicalBoard = PhysicalBoard.Board()
      val dynamicScene = DynamicScene(model)
      <.svg.g(
        Backdrop(),
        <.svg.g(
          ^.svg.transform := "translate(400,400),scale(90)",
          physicalBoard,
          dynamicScene
        )
      )
    }.build

  def apply(model: GameScreenModel) = component(model)

}