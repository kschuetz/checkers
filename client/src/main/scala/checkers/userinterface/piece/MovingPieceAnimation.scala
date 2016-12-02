package checkers.userinterface.piece

import checkers.consts._
import checkers.core.Board
import checkers.util.Easing
import japgolly.scalajs.react._

object MovingPieceAnimation {

  case class Props(piece: Occupant,
                   fromSquare: Int,
                   toSquare: Int,
                   progress: Double,
                   rotationDegrees: Double)

}

class MovingPieceAnimation(physicalPiece: PhysicalPiece) {

  import MovingPieceAnimation._

  class MovingPieceAnimationBackend($: BackendScope[Props, Unit]) {
    def render(props: Props): ReactElement = {
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

      val pieceElement = physicalPiece.component(physicalPieceProps)

      pieceElement
    }
  }

  val component = ReactComponentB[Props]("MovingPieceAnimation")
    .renderBackend[MovingPieceAnimationBackend]
    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, _) =>
      val result = scope.props != nextProps
      CallbackTo.pure(result)
    }
    .build

}