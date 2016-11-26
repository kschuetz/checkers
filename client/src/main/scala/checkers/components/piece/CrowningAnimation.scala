package checkers.components.piece

import checkers.consts._
import checkers.core.Board
import checkers.util.Easing
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object CrowningAnimation {

  case class Props(color: Color,
                   square: Int,
                   progress: Double)

  class CrowningAnimationBackend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      val piece = if(props.color == DARK) DARKMAN else LIGHTMAN

      val entryPoint = AnimationHelpers.exitPoint(piece, props.square)
      val dest = Board.squareCenter(props.square)

      val t = Easing.easeInQuad(props.progress)
      val x = entryPoint.x + (dest.x - entryPoint.x) * t
      val y = entryPoint.y + (dest.y - entryPoint.y) * t

      val topProps = PhysicalPieceProps.default.copy(piece = piece,
        x = x,
        y = y)

      val topPiece = PhysicalPiece.apply(topProps)

      val bottomProps = PhysicalPieceProps.default.copy(piece = piece,
        x = dest.x,
        y = dest.y)

      val bottomPiece = PhysicalPiece.apply(bottomProps)

      <.svg.g(
        bottomPiece,
        topPiece
      )
    }
  }


  val component = ReactComponentB[Props]("CrowningAnimation")
    .renderBackend[CrowningAnimationBackend]
    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, _) =>
      val result = scope.props != nextProps
      CallbackTo.pure(result)
    }
    .build

  def apply(props: Props) = component(props)

}