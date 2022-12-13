package checkers.userinterface.chrome

import checkers.consts._
import checkers.userinterface.piece.{PhysicalPiece, PhysicalPieceProps}
import japgolly.scalajs.react._

object PieceAvatar {

  case class Props(side: Side,
                   isPlayerTurn: Boolean,
                   x: Double,
                   y: Double,
                   scale: Double = 1.0)

}

class PieceAvatar(physicalPiece: PhysicalPiece) {

  import PieceAvatar._

  val create = ScalaComponent.builder[Props]("PieceAvatar")
    .render_P { props =>
      val pieceProps = PhysicalPieceProps.default.copy(
        piece = if (props.side == DARK) DARKMAN else LIGHTMAN,
        ghost = !props.isPlayerTurn,
        scale = props.scale,
        x = props.x,
        y = props.y
      )
      physicalPiece.create(pieceProps)
    }
    .build

}
