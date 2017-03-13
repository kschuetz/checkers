package checkers.userinterface

import checkers.userinterface.board.PhysicalBoard
import checkers.core.GameModelReader
import checkers.util.Point
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.VdomAttr
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.raw.{SVGGElement, SVGLocatable}

object SceneFrame {

  type Callbacks = BoardCallbacks

  case class Props(gameModel: GameModelReader,
                   callbacks: Callbacks,
                   sceneContainerContext: SceneContainerContext,
                   widthPixels: Int,
                   heightPixels: Int)

}

class SceneFrame(physicalBoard: PhysicalBoard,
                 dynamicScene: DynamicScene) {

  import SceneFrame._

  private val Backdrop = ScalaComponent.build[(Int, Int)]("Backdrop")
    .render_P { case (width, height) =>
      <.svg.rect(
        VdomAttr.ClassName := "backdrop",
        ^.svg.x := 0,
        ^.svg.y := 0,
        ^.svg.width := width,
        ^.svg.height := height
      )
    }.build

  class Backend($: BackendScope[Props, Unit]) {
    val playfieldRef = Ref[SVGGElement]("playfield")

    def render(props: Props): VdomElement = {
      val Props(model, callbacks, sceneContainerContext, widthPixels, heightPixels) = props
      val screenToBoard = makeScreenToBoard(sceneContainerContext)

      val boardRotation = model.getBoardRotation
      val rotateTransform = if (boardRotation != 0) s",rotate($boardRotation)" else ""

      val dynamicSceneProps = DynamicScene.Props(model, callbacks, sceneContainerContext, screenToBoard)

      val transform = if (widthPixels == heightPixels) {
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

      val physicalBoardElement = physicalBoard.create()
      val dynamicSceneElement = dynamicScene.create(dynamicSceneProps)
      <.svg.g(
        Backdrop((widthPixels, heightPixels)),
        <.svg.g(
          ^.ref := playfieldRef,
          ^.svg.transform := transform,
          physicalBoardElement,
          dynamicSceneElement
        )
      )
    }

    def makeScreenToBoard(sceneContext: SceneContainerContext): Point => Point = { screen: Point =>
      var result = screen
      $.refs(playfieldRef.name).foreach { node =>
        val target = node.getDOMNode.asInstanceOf[SVGLocatable]
        result = sceneContext.screenToLocal(target)(screen)
      }
      result
    }

  }

  val create = ScalaComponent.build[Props]("SceneFrame")
    .renderBackend[Backend]
    .build


  private def scaleForDimension(dimensionPixels: Int): Double = {
    // when board is 800 x 800, scene needs to be scaled 90 times
    (dimensionPixels * 90) / 800.0
  }

}