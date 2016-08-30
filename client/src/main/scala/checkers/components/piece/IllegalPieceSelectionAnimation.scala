package checkers.components.piece

import checkers.consts._
import checkers.core.Board
import checkers.util.Easing
import japgolly.scalajs.react._

object IllegalPieceSelectionAnimation {

  case class Props(piece: Occupant,
                   squareIndex: Int,
                   progress: Double)

  class IllegalPieceSelectionAnimationBackend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      // TODO

//      val physicalPieceProps = PhysicalPieceProps.default.copy(piece = props.piece,
//        x = x,
//        y = y)
//      val physicalPiece = PhysicalPiece.apply(physicalPieceProps)
//
//      physicalPiece
      null
    }
  }


  val component = ReactComponentB[Props]("IllegalPieceSelectionAnimation")
    .renderBackend[IllegalPieceSelectionAnimationBackend]
    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, _) =>
      val result = scope.props != nextProps
      CallbackTo.pure(result)
    }
    .build

  def apply(props: Props) = component(props)

}