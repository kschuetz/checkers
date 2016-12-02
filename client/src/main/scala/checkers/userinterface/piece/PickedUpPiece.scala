package checkers.userinterface.piece

import checkers.consts._
import checkers.util.SvgHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object PickedUpPiece extends SvgHelpers {

  type Model = checkers.core.PickedUpPiece

  case class Props(model: Model,
                   rotationDegrees: Double)

}

class PickedUpPiece(physicalPiece: PhysicalPiece) {
  import PickedUpPiece._

  class PickedUpPieceBackend($: BackendScope[Props, Unit]) {
    def render(props: Props): ReactElement = {
      val model = props.model
      val center = model.movePos // + model.grabOffset

      val physicalPieceProps = PhysicalPieceProps.default.copy(piece = model.piece,
        x = center.x,
        y = center.y,
        rotationDegrees = props.rotationDegrees,
        scale = 1.1)

      val pieceElement = physicalPiece.component(physicalPieceProps)

      val side = SIDE(model.piece)
      val classes = if(side == DARK) "picked-up-piece dark" else "picked-up-piece light"

      <.svg.g(
        ^.`class` := classes,
        pieceElement
      )

    }
  }

  val component = ReactComponentB[Props]("PickedUpPiece")
    .renderBackend[PickedUpPieceBackend]
    .build


}