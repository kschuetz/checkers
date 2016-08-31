package checkers.components.piece

import checkers.consts._
import checkers.core.Board
import checkers.geometry.Point
import japgolly.scalajs.react._

object IllegalPieceSelectionAnimation {

  case class Props(piece: Occupant,
                   squareIndex: Int,
                   progress: Double)

  class IllegalPieceSelectionAnimationBackend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      val t = math.Pi * props.progress
      val xoffset = 0.07 * (1 - props.progress) * math.sin(13 * t)
      val yoffset = 0 //0.15 * math.sin(7.5 * t)

      val pt = Board.squareCenter(props.squareIndex) + Point(xoffset, yoffset)

      val physicalPieceProps = PhysicalPieceProps.default.copy(piece = props.piece,
        x = pt.x,
        y = pt.y)
      val physicalPiece = PhysicalPiece.apply(physicalPieceProps)

      physicalPiece
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