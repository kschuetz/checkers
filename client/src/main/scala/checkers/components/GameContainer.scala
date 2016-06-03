package checkers.components

import checkers.geometry.Point
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.{SVGElement, SVGSVGElement}


object GameContainer {

  class Backend($: BackendScope[SceneFrame.Properties, Unit]) {

    var sceneRenderContext = SceneRenderContext.default

    def start = Callback {
      println("mounted")
      val target = $.getDOMNode()
      println(target)
      sceneRenderContext = SceneRenderContext.fromSVGElement(target.asInstanceOf[SVGSVGElement])

      scala.scalajs.js.timers.setTimeout(1000){
        val testPt = Point(20, 20)
        val res = sceneRenderContext.cursorToLocal(testPt)
        println(res)
      }

    }

    def handleMouseMove(event: ReactMouseEvent) = Callback {
      if(event.altKey) {
        val pt = Point(event.clientX, event.clientY)
        val transformed = sceneRenderContext.cursorToLocal(pt)
        println(s"$pt --- $transformed")
      }

    }

    def render(props: SceneFrame.Properties) = {
      <.svg.svg(
        ^.id := "game-container",
        ^.svg.width := "800px",
        ^.svg.height := "800px",
        ^.onMouseMove ==> handleMouseMove,
        SceneFrame(props)
      )
    }
  }

  val component = ReactComponentB[SceneFrame.Properties]("GameContainer")
    .renderBackend[Backend]
    .componentDidMount(_.backend.start)
    .build


  def apply(props: SceneFrame.Properties) = component(props)
}