package checkers.components

import checkers.components.board.PhysicalBoard
import checkers.components.piece.PieceCallbacks
import checkers.models.GameScreenModel
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactAttr
import japgolly.scalajs.react.vdom.prefix_<^._

object SceneFrame {

  type Callbacks = PieceCallbacks

  type Props = (GameScreenModel, Callbacks)

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

  val component = ReactComponentB[Props]("SceneFrame")
    .render_P { case props@(model, callbacks) =>
      val physicalBoard = PhysicalBoard.Board()
      val dynamicScene = DynamicScene(props)
      <.svg.g(
        Backdrop(),
        <.svg.g(
          ^.svg.transform := "translate(400,400),scale(90)",
          physicalBoard,
          dynamicScene
        )
      )
    }.build

  def apply(model: Props) = component(model)

}