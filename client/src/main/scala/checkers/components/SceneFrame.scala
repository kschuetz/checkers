package checkers.components

import checkers.components.board.PhysicalBoard
import checkers.components.piece.PieceCallbacks
import checkers.geometry.Point
import checkers.models.GameScreenModel
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactAttr
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.{SVGLocatable, SVGSVGElement}

object SceneFrame {

  type Callbacks = PieceCallbacks

  type Props = (GameScreenModel, Callbacks, SceneContainerContext)

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


  def handleMouseMove(sceneContainerContext: SceneContainerContext)(event: ReactMouseEvent) = Callback {

    if(event.altKey) {
      val pt = Point(event.clientX, event.clientY)
//      val context = SceneRenderContext.fromSVGElement(event.target.asInstanceOf[SVGSVGElement])
//      val transformed = context.cursorToLocal(pt)
      val transformed = sceneContainerContext.screenToLocal(event.target.asInstanceOf[SVGLocatable])(pt)
      println(s"screen: $pt")
      println(s"local: $transformed")
    }

  }

  val component = ReactComponentB[Props]("SceneFrame")
    .render_P { case props@(model, callbacks, sceneContainerContext) =>
      val physicalBoard = PhysicalBoard.Board()
      val dynamicScene = DynamicScene(props)
      <.svg.g(
        Backdrop(),
        <.svg.g(
          ^.svg.transform := "translate(400,400),scale(90)",
          ^.onMouseMove ==> handleMouseMove(sceneContainerContext),
          physicalBoard,
          dynamicScene
        )
      )
    }.build

  def apply(model: Props) = component(model)

}