package checkers.userinterface

import checkers.core.GameModelReader
import checkers.userinterface.board.PhysicalBoard
import checkers.util.Point
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{svg_<^ => svg}
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

  private val Backdrop = ScalaComponent.builder[(Int, Int)]("Backdrop")
    .render_P { case (width, height) =>
      svg.<.rect(
//        VdomAttr.ClassName := "backdrop",
        ^.`class` := "backdrop",
        svg.^.x := 0.asInstanceOf[JsNumber],
        svg.^.y := 0.asInstanceOf[JsNumber],
        svg.^.width := width.asInstanceOf[JsNumber],
        svg.^.height := height.asInstanceOf[JsNumber]
      )
    }.build

  class Backend($: BackendScope[Props, Unit]) {
    private var playfieldRef: SVGGElement = _

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
      svg.<.g(
        Backdrop((widthPixels, heightPixels)),
        svg.<.g.ref(playfieldRef = _)(
          svg.^.transform := transform,
          physicalBoardElement,
          dynamicSceneElement
        )
      )
    }

    def makeScreenToBoard(sceneContext: SceneContainerContext): Point => Point = { screen: Point =>
      var result = screen
      val target = playfieldRef.asInstanceOf[SVGLocatable]
      result = sceneContext.screenToLocal(target)(screen)
      result
    }

  }

  val create = ScalaComponent.builder[Props]("SceneFrame")
    .renderBackend[Backend]
    .build


  private def scaleForDimension(dimensionPixels: Int): Double = {
    // when board is 800 x 800, scene needs to be scaled 90 times
    (dimensionPixels * 90) / 800.0
  }

}