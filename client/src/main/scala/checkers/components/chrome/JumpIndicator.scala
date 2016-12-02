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

object JumpIndicator extends SvgHelpers {

  private val jumpArrowPath = """
    m -0.52022338,0.16760845
    c 0.47624525,-0.53722476 0.87339974,-0.1982597 0.91866233,-0.16931034
    l 0.0618484,-0.18267684 0.0967177,0.37715305 -0.43578221,-0.0394764 0.1766451,-0.0704297
    c -0.39239361,-0.24518315 -0.64785322,0.10269894 -0.74303663,0.15877526 -0.009924,-0.0361076 -0.0194947,-0.0611194 -0.0750547,-0.074035
    z
  """

  private val JumpArrow = ReactComponentB[Side]("JumpIndicatorArrow")
    .render_P { oppositeSide =>
      val classes = s"jump-indicator-arrow ${CssHelpers.playerSideClass(oppositeSide)}"
      <.svg.g(
        <.svg.path(
          ^.`class` := classes,
          ^.svg.d := jumpArrowPath
        )
      )
    }
    .build

  private val OpponentAvatar = ReactComponentB[Side]("JumpIndicatorOpponentAvatar")
    .render_P { side =>
      val pieceProps = PhysicalPieceProps.default.copy(
        piece = if (side == DARK) DARKMAN else LIGHTMAN,
        ghost = false,
        scale = 1,
        x = 0,
        y = 0
      )
      PhysicalPiece.apply(pieceProps)
    }
    .build


  case class Props(oppositeSide: Side,
                   x: Double = 0.0,
                   y: Double = 0.0,
                   scale: Double)

  val component = ReactComponentB[Props]("JumpIndicator")
    .render_P { props =>

      val opponentAvatar = OpponentAvatar(props.oppositeSide)
      val jumpArrow = JumpArrow(props.oppositeSide)

      <.svg.g(
        ^.svg.transform := s"translate(${props.x},${props.y}),scale(${props.scale})",
        <.titleTag("You are required to jump on this move"),
        opponentAvatar,
        <.svg.g(
          ^.svg.transform := "translate(0,-0.61)",
          jumpArrow
        )
      )

    }
    .build

}