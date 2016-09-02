package checkers.components.chrome

import checkers.components.SceneFrame
import checkers.components.piece.{PhysicalPiece, PhysicalPieceProps}
import checkers.consts._
import checkers.core.GameModelReader
import checkers.util.CssHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.SVGSVGElement

import scala.scalajs.js

object PieceAvatar {

  case class Props(color: Color,
                   isPlayerTurn: Boolean,
                   x: Double,
                   y: Double,
                   scale: Double = 1.0)

  val component = ReactComponentB[Props]("PieceAvatar")
    .render_P { props =>
      val pieceProps = PhysicalPieceProps.default.copy(
        piece = if(props.color == DARK) DARKMAN else LIGHTMAN,
        ghost = !props.isPlayerTurn,
        scale = props.scale,
        x = props.x,
        y = props.y
      )
      PhysicalPiece.apply(pieceProps)
    }
    .build

}