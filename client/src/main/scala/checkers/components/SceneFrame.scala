package checkers.components

import checkers.components.board.PhysicalBoard
import checkers.components.piece.PieceCallbacks
import checkers.geometry.Point
import checkers.models.{GameScreenModel, GameScreenModelReader}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactAttr
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.{SVGGElement, SVGLocatable}

object SceneFrame {

  type Callbacks = PieceCallbacks

  type Props = (GameScreenModelReader, Callbacks, SceneContainerContext)

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


  class SceneFrameBackend($: BackendScope[Props, Unit]) {
    val playfieldRef = Ref[SVGGElement]("playfield")

    def render(props: Props) = {
      val (model, callbacks, sceneContainerContext) = props
      val physicalBoard = PhysicalBoard.Board()
      val screenToBoard = makeScreenToBoard(sceneContainerContext)
      val dynamicScene = DynamicScene((props._1, props._2, props._3, screenToBoard))
      <.svg.g(
        Backdrop(),
        <.svg.g(
          ^.ref := playfieldRef,
          ^.svg.transform := "translate(400,400),scale(90)",
          //^.onMouseMove ==> handleMouseMove,
          physicalBoard,
          dynamicScene
        )
      )
    }

    def makeScreenToBoard(sceneContext: SceneContainerContext): Point => Point = { screen: Point =>
      var result = screen
      $.refs(playfieldRef.name).foreach { node =>
        val target = node.getDOMNode().asInstanceOf[SVGLocatable]
        result = sceneContext.screenToLocal(target)(screen)
      }
      result
    }

    def handleMouseMove(event: ReactMouseEvent) = $.props.map {  props =>
      val context = props._3
      if(event.altKey) {
        val pt = Point(event.clientX, event.clientY)
        $.refs(playfieldRef.name).foreach { node =>
          val target = node.getDOMNode().asInstanceOf[SVGLocatable]
          val transformed = context.screenToLocal(target)(pt)
          println(s"screen: $pt")
          println(s"local: $transformed")
        }
      }
    }
  }

  val component = ReactComponentB[Props]("SceneFrame")
      .renderBackend[SceneFrameBackend]
      .build


  def apply(model: Props) = component(model)

}