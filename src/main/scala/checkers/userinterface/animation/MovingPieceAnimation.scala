package checkers.userinterface.animation

import checkers.consts._
import checkers.core.Board
import checkers.userinterface.piece.{PhysicalPiece, PhysicalPieceProps}
import checkers.util.Easing
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.VdomElement

object MovingPieceAnimation {

  case class Props(piece: Occupant,
                   fromSquare: Int,
                   toSquare: Int,
                   progress: Double,
                   rotationDegrees: Double)

}

class MovingPieceAnimation(physicalPiece: PhysicalPiece) {

  import MovingPieceAnimation._

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props): VdomElement = {
      val t = Easing.easeInOutQuart(props.progress)
      val ptA = Board.squareCenter(props.fromSquare)
      val ptB = Board.squareCenter(props.toSquare)

      val x0 = ptA.x
      val x = x0 + t * (ptB.x - x0)

      val y0 = ptA.y
      val y = y0 + t * (ptB.y - y0)

      val physicalPieceProps = PhysicalPieceProps.default.copy(piece = props.piece,
        x = x,
        y = y,
        rotationDegrees = props.rotationDegrees)

      val pieceElement = physicalPiece.create(physicalPieceProps)

      pieceElement
    }
  }

  val create = ScalaComponent.builder[Props]("MovingPieceAnimation")
    .renderBackend[Backend]
    .shouldComponentUpdate { x => CallbackTo.pure(x.cmpProps(_ != _)) }
    .build

}
