package checkers.components.chrome

import checkers.components.SceneFrame
import checkers.models.GameModelReader
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.SVGSVGElement

object TopChrome {

  case class Props(gameModel: GameModelReader,
                   widthPixels: Int,
                   heightPixels: Int)

  class TopChromeBackend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      <.svg.svg(
        ^.id := "top-chrome",
        ^.svg.width := s"${props.widthPixels}px",
        ^.svg.height := s"${props.heightPixels}px"
        //^.onMouseMove ==> handleMouseMove,
        //SceneFrame((props._1, props._2, state))
      )
    }
  }

  val component = ReactComponentB[Props]("TopChrome")
    .renderBackend[TopChromeBackend]
//    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, _) =>
//      val result = scope.props != nextProps
//      CallbackTo.pure(result)
//    }
    .build

  def apply(props: Props) = component(props)


}