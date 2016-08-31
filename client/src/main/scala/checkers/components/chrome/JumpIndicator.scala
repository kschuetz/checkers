package checkers.components.chrome

import checkers.components.SceneFrame
import checkers.components.piece.{PhysicalPiece, PhysicalPieceProps}
import checkers.consts._
import checkers.geometry.Point
import checkers.models.GameModelReader
import checkers.util.{CssHelpers, SvgHelpers}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.SVGSVGElement

import scala.scalajs.js

object JumpIndicator extends SvgHelpers {

  private val jumpArrowPath = """
    m -0.52022338,0.16760845
    c 0.47624525,-0.53722476 0.87339974,-0.1982597 0.91866233,-0.16931034
    l 0.0618484,-0.18267684 0.0967177,0.37715305 -0.43578221,-0.0394764 0.1766451,-0.0704297
    c -0.39239361,-0.24518315 -0.64785322,0.10269894 -0.74303663,0.15877526 -0.009924,-0.0361076 -0.0194947,-0.0611194 -0.0750547,-0.074035
    z
  """

  private val JumpArrow = ReactComponentB[Color]("JumpIndicatorArrow")
    .render_P { opponentColor =>
      val classes = s"jump-indicator-arrow ${CssHelpers.playerColorClass(opponentColor)}"
      <.svg.g(
        <.svg.path(
          ^.`class` := classes,
          ^.svg.d := jumpArrowPath
        )
      )
    }
    .build

  private val OpponentAvatar = ReactComponentB[Color]("JumpIndicatorOpponentAvatar")
    .render_P { color =>
      val pieceProps = PhysicalPieceProps.default.copy(
        piece = if (color == DARK) DARKMAN else LIGHTMAN,
        ghost = false,
        scale = 1,
        x = 0,
        y = 0
      )
      PhysicalPiece.apply(pieceProps)
    }
    .build


  case class Props(opponentColor: Color,
                   x: Double = 0.0,
                   y: Double = 0.0,
                   scale: Double)

  val component = ReactComponentB[Props]("JumpIndicator")
    .render_P { props =>

      val opponentAvatar = OpponentAvatar(props.opponentColor)
      val jumpArrow = JumpArrow(props.opponentColor)

      <.svg.g(
        ^.svg.transform := s"translate(${props.x},${props.y}),scale(${props.scale})",
        opponentAvatar,
        <.svg.g(
          ^.svg.transform := "translate(0,-0.61)",
          jumpArrow
        )

      )

    }
    .build

}