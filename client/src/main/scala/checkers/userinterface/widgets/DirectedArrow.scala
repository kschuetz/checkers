package checkers.userinterface.widgets

import checkers.util.{Point, SvgHelpers}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{ svg_<^ => svg }

object DirectedArrow {
  case class Props(source: Point,
                   dest: Point,
                   headLength: Double,
                   headWidth: Double,
                   baseWidth: Double,
                   sourceMargin: Double = 0.0,
                   destMargin: Double = 0.0,
                   extraClasses: Map[String, Boolean] = Map.empty)

  private val up = Point(0, -1)
}

class DirectedArrow(arrow: Arrow) extends SvgHelpers {
  import DirectedArrow._

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props): VdomElement = {
      val center = props.dest - props.source
      val length = center.magnitude
      val dir = center / length
      val angle = {
        val rad = math.acos(dir.dot(up))
        val deg = math.toDegrees(rad)
        if(dir.x < 0) -deg else deg
      }
      val arrowProps = Arrow.Props(
        totalLength = length - props.destMargin - props.sourceMargin,
        headLength = props.headLength,
        headWidth = props.headWidth,
        baseWidth = props.baseWidth,
        baseOffset = -props.sourceMargin,
        extraClasses = props.extraClasses)
      val arrowElement = arrow.create(arrowProps)
      svg.<.g(
        svg.^.transform := s"translate(${props.source.x},${props.source.y}),rotate($angle)",
        arrowElement
      )
    }
  }

  val create = ScalaComponent.build[Props]("DirectedArrow")
    .renderBackend[Backend]
    .shouldComponentUpdate { x => CallbackTo.pure(x.currentProps != x.nextProps) }
    .build
}