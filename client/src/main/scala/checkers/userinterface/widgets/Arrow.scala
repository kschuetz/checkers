package checkers.userinterface.widgets

import checkers.util.{Point, SvgHelpers}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.VdomTagOf
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.svg.Polygon

object Arrow {
  case class Props(totalLength: Double,
                   headLength: Double,
                   headWidth: Double,
                   baseWidth: Double,
                   baseOffset: Double = 0,
                   extraClasses: Map[String, Boolean] = Map.empty)
}

class Arrow extends SvgHelpers {
  import Arrow._

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props): VdomElement = {
      val yOffset = props.baseOffset

      val x0 = 0d
      val x1 = props.baseWidth / 2
      val x2 = props.headWidth / 2
      val y0 = yOffset - props.totalLength
      val y1 = yOffset + props.headLength - props.totalLength
      val y2 = yOffset

      val pathString = pointsToPathString(
        Point(x0, y0),
        Point(x2, y1),
        Point(x1, y1),
        Point(x1, y2),
        Point(-x1, y2),
        Point(-x1, y1),
        Point(-x2, y1))

      <.svg.polygon(
        ^.classSetM(props.extraClasses),
        ^.svg.points := pathString
      )
    }
  }

  val create = ScalaComponent.build[Props]("ArrowWidget")
    .renderBackend[Backend]
    .shouldComponentUpdateConst { case ShouldComponentUpdate(scope, nextProps, _) =>
      val result = scope.props != nextProps
      CallbackTo.pure(result)
    }
    .build
}