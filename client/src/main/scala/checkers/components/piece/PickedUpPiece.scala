package checkers.components.piece

import checkers.consts._
import checkers.util.SvgHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object PickedUpPiece extends SvgHelpers {

  type Model = checkers.core.PickedUpPiece

  case class Props(model: Model,
                   rotationDegrees: Double)

  class PickedUpPieceBackend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      val model = props.model
      val center = model.movePos // + model.grabOffset

      val physicalPieceProps = PhysicalPieceProps.default.copy(piece = model.piece,
        x = center.x,
        y = center.y,
        rotationDegrees = props.rotationDegrees,
        scale = 1.1)

      val physicalPiece = PhysicalPiece.apply(physicalPieceProps)

      val side = SIDE(model.piece)
      val classes = if(side == DARK) "picked-up-piece dark" else "picked-up-piece light"

      <.svg.g(
        ^.`class` := classes,
        physicalPiece
      )

    }
  }

  val component = ReactComponentB[Props]("PickedUpPiece").renderBackend[PickedUpPieceBackend].build

  def apply(props: Props) = component(props)

}