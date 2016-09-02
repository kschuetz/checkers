package checkers.components

import checkers.core.{GameModelReader, ScreenLayoutSettings}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.SVGSVGElement


object SceneContainer {

  type Callbacks = BoardCallbacks

  case class Props(gameModel: GameModelReader,
                   screenLayoutSettings: ScreenLayoutSettings,
                   callbacks: Callbacks)

  class Backend($: BackendScope[Props, SceneContainerContext]) {

    def start = {
      val target = $.getDOMNode()
      val sceneContainerContext = new MountedSceneContainerContext(target.asInstanceOf[SVGSVGElement])
      $.setState(sceneContainerContext)
    }

    def render(props: Props, state: SceneContainerContext) = {
      val sceneFrameProps = SceneFrame.Props(props.gameModel, props.callbacks, state)
      <.svg.svg(
        ^.id := "game-scene",
        ^.svg.width := "800px",
        ^.svg.height := "800px",
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


  def apply(props: Props) = component(props)
}