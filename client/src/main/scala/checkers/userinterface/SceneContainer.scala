package checkers.userinterface

import checkers.core.{GameModelReader, ScreenLayoutSettings}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{svg_<^ => svg}
import org.scalajs.dom
import org.scalajs.dom.raw.SVGSVGElement

import scala.scalajs.js


object SceneContainer {

  type Callbacks = BoardCallbacks

  case class Props(gameModel: GameModelReader,
                   screenLayoutSettings: ScreenLayoutSettings,
                   callbacks: Callbacks)

}

class SceneContainer(sceneFrame: SceneFrame) {

  import SceneContainer._

  class Backend($: BackendScope[Props, SceneContainerContext]) {

    def start: Callback = for {
      target <- $.getDOMNode
      sceneContainerContext = new MountedSceneContainerContext(target.asInstanceOf[SVGSVGElement])
      cb <- $.setState(sceneContainerContext)
    } yield cb

    def render(props: Props, state: SceneContainerContext): VdomElement = {
      val gameSceneWidth = props.screenLayoutSettings.GameSceneWidthPixels
      val gameSceneHeight = props.screenLayoutSettings.GameSceneHeightPixels
      val sceneFrameProps = SceneFrame.Props(props.gameModel, props.callbacks, state, gameSceneWidth, gameSceneHeight)
      svg.<.svg(
        ^.id := "game-scene",
        svg.^.width := s"${gameSceneWidth}px",
        svg.^.height := s"${gameSceneHeight}px",
        //^.onMouseMove ==> handleMouseMove,
        sceneFrame.create(sceneFrameProps)
      )
    }
  }

  val create = ScalaComponent.builder[Props]("SceneContainer")
    .initialState[SceneContainerContext](NullSceneContainerContext)
    .renderBackend[Backend]
    .componentDidMount(_.backend.start)
    .build

}