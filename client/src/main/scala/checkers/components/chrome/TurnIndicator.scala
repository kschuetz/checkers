package checkers.components.chrome

import checkers.components.SceneFrame
import checkers.components.piece.{PhysicalPiece, PhysicalPieceProps}
import checkers.consts._
import checkers.core.GameModelReader
import checkers.geometry.Point
import checkers.util.{CssHelpers, SvgHelpers}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.SVGSVGElement

import scala.scalajs.js

object TurnIndicator extends SvgHelpers {

  case class Props(side: Side,
                   pointsRight: Boolean = false,
                   endingTurn: Boolean = false,
                   x: Double = 0.0,
                   y: Double = 0.0,
                   scale: Double = 1.0,
                   baseThickness: Double = 0.68,
                   baseLength: Double = 0.75,
                   headLength: Double = 1.0)

  val component = ReactComponentB[Props]("TurnIndicator")
    .render_P { props =>
      val x0 = 0d
      val (x1, x2) = if(props.pointsRight) {
        (-props.headLength, -(props.headLength + props.baseLength))
      } else {
        (props.headLength, props.headLength + props.baseLength)
      }
      val y0 = 0d
      val y1 = props.baseThickness / 2
      val y2 = 1d

      val pathString = pointsToPathString(
        Point(x0, y0),
        Point(x1, y2),
        Point(x1, y1),
        Point(x2, y1),
        Point(x2, -y1),
        Point(x1, -y1),
        Point(x1, -y2))

      val arrow = <.svg.polygon(
        ^.classSet1("turn-indicator", "ending-turn" -> props.endingTurn),
        ^.svg.points := pathString
      )

      <.svg.g(
        ^.svg.transform := s"translate(${props.x},${props.y}),scale(${props.scale})",
        arrow
      )

    }
    .build

}