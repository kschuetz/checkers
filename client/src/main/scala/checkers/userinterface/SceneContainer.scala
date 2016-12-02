package checkers.userinterface

import checkers.core.{GameModelReader, ScreenLayoutSettings}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.SVGSVGElement


object SceneContainer {

  type Callbacks = BoardCallbacks

  case class Props(gameModel: GameModelReader,
                   screenLayoutSettings: ScreenLayoutSettings,
                   callbacks: Callbacks)

}

class SceneContainer {

  import SceneContainer._

  class Backend($: BackendScope[Props, SceneContainerContext]) {

    def start: Callback = {
      val target = $.getDOMNode()
      val sceneContainerContext = new MountedSceneContainerContext(target.asInstanceOf[SVGSVGElement])
      $.setState(sceneContainerContext)
    }

    def render(props: Props, state: SceneContainerContext) = {
      val gameSceneWidth = props.screenLayoutSettings.GameSceneWidthPixels
      val gameSceneHeight = props.screenLayoutSettings.GameSceneHeightPixels
      val sceneFrameProps = SceneFrame.Props(props.gameModel, props.callbacks, state, gameSceneWidth, gameSceneHeight)
      <.svg.svg(
        ^.id := "game-scene",
        ^.svg.width := s"${gameSceneWidth}px",
        ^.svg.height := s"${gameSceneHeight}px",
        //^.onMouseMove ==> handleMouseMove,
        SceneFrame(sceneFrameProps)
      )
    }
  }

  val component = ReactComponentB[Props]("SceneContainer")
    .initialState[SceneContainerContext](NullSceneContainerContext)
    .renderBackend[Backend]
    .componentDidMount(_.backend.start)
    .build

}