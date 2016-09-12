package checkers.components

import checkers.components.board.PhysicalBoard
import checkers.core.GameModelReader
import checkers.geometry.Point
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactAttr
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.{SVGGElement, SVGLocatable}

object SceneFrame {

  type Callbacks = BoardCallbacks

  case class Props(gameModel: GameModelReader,
                   callbacks: Callbacks,
                   sceneContainerContext: SceneContainerContext,
                   widthPixels: Int,
                   heightPixels: Int)

  val Backdrop = ReactComponentB[(Int, Int)]("Backdrop")
    .render_P { case (width, height) =>
      <.svg.rect(
        ReactAttr.ClassName := "backdrop",
        ^.svg.x := 0,
        ^.svg.y := 0,
        ^.svg.width := width,
        ^.svg.height := height
      )
    }.build

  class SceneFrameBackend($: BackendScope[Props, Unit]) {
    val playfieldRef = Ref[SVGGElement]("playfield")

    def render(props: Props) = {
      val Props(model, callbacks, sceneContainerContext, widthPixels, heightPixels) = props
      val physicalBoard = PhysicalBoard.Board()
      val screenToBoard = makeScreenToBoard(sceneContainerContext)

      val boardRotation = props.gameModel.getBoardRotation
      val rotateTransform = if(boardRotation != 0) s",rotate($boardRotation)" else ""

      val dynamicSceneProps = DynamicScene.Props(props.gameModel, props.callbacks, props.sceneContainerContext, screenToBoard)

      val transform = if(widthPixels == heightPixels) {
        val translate = widthPixels / 2.0
        val scale = scaleForDimension(widthPixels)
        s"translate($translate,$translate),scale($scale)$rotateTransform"
      } else {
        val translateX = widthPixels / 2.0
        val translateY = heightPixels / 2.0
        val scaleX = scaleForDimension(widthPixels)
        val scaleY = scaleForDimension(heightPixels)
        s"translate($translateX,$translateY),scale($scaleX,$scaleY)$rotateTransform"
      }

      val dynamicScene = DynamicScene(dynamicSceneProps)
      <.svg.g(
        Backdrop((widthPixels, heightPixels)),
        <.svg.g(
          ^.ref := playfieldRef,
          ^.svg.transform := transform,
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
      val context = props.sceneContainerContext
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

  private def scaleForDimension(dimensionPixels: Int): Double = {
    // when board is 800 x 800, scene needs to be scaled 90 times
    (dimensionPixels * 90) / 800.0
  }

}