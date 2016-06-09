package checkers.components.piece

import checkers.core.{Dark, Light}
import checkers.util.SvgHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object GhostPiece extends SvgHelpers {

  type Model = checkers.models.GhostPiece

  type Props = Model

  class GhostPieceBackend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      val model = props
      val center = model.movePos + model.grabOffset

      val physicalPieceProps = PhysicalPieceProps.default.copy(piece = model.piece,
        x = center.x,
        y = center.y)

      val physicalPiece = PhysicalPiece.apply(physicalPieceProps)

      val classes = model.piece.color match {
        case Dark => "ghost-piece dark"
        case Light => "ghost-piece light"
      }

      <.svg.g(
        ^.`class` := classes,
        physicalPiece
      )

    }
  }

  val component = ReactComponentB[Props]("GhostPiece").renderBackend[GhostPieceBackend].build

  def apply(props: Props) = component(props)

}