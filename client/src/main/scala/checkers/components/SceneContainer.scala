package checkers.components

import checkers.models.GameModelReader
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.SVGSVGElement


object SceneContainer {

  type Callbacks = PieceCallbacks

  type Props = (GameModelReader, Callbacks)

  class Backend($: BackendScope[Props, SceneContainerContext]) {

//    var sceneRenderContext = SceneRenderContext.default

//    var sceneContainerContext: SceneContainerContext = NullSceneContainerContext   // will be initialized after mounting

//    def start = Callback {
//      println("mounted")
//      val target = $.getDOMNode()
//      println(target)
//      sceneRenderContext = SceneRenderContext.fromSVGElement(target.asInstanceOf[SVGSVGElement])
//
//      sceneContainerContext = new MountedSceneContainerContext(target.asInstanceOf[SVGSVGElement])
//
//      scala.scalajs.js.timers.setTimeout(1000){
//        val testPt = Point(20, 20)
//        val res = sceneRenderContext.cursorToLocal(testPt)
//        println(res)
//      }
//
//    }

//    def handleMouseMove(event: ReactMouseEvent) = Callback {
//      if(event.altKey) {
//        val pt = Point(event.clientX, event.clientY)
//        val transformed = sceneRenderContext.cursorToLocal(pt)
//        println(s"$pt --- $transformed")
//      }
//
//    }

    def start = {
      val target = $.getDOMNode()
      val sceneContainerContext = new MountedSceneContainerContext(target.asInstanceOf[SVGSVGElement])
      $.setState(sceneContainerContext)
    }

    def render(props: Props, state: SceneContainerContext) = {
      <.svg.svg(
        ^.id := "game-scene",
        ^.svg.width := "800px",
        ^.svg.height := "800px",
        //^.onMouseMove ==> handleMouseMove,
        SceneFrame((props._1, props._2, state))
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