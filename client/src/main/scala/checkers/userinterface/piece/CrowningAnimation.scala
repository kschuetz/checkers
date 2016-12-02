package checkers.userinterface.piece

import checkers.consts._
import checkers.core.Board
import checkers.util.Easing
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object CrowningAnimation {

  case class Props(side: Side,
                   square: Int,
                   progress: Double,
                   rotationDegrees: Double)

  class CrowningAnimationBackend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      val piece = if(props.side == DARK) DARKMAN else LIGHTMAN

      val entryPoint = AnimationHelpers.exitPoint(piece, props.square)
      val dest = Board.squareCenter(props.square)

      val t = Easing.easeInQuad(props.progress)
      val x = entryPoint.x + (dest.x - entryPoint.x) * t
      val y = entryPoint.y + (dest.y - entryPoint.y) * t

      val topProps = PhysicalPieceProps.default.copy(piece = piece,
        x = x,
        y = y,
        rotationDegrees = props.rotationDegrees)

      val topPiece = PhysicalPiece.apply(topProps)

      val bottomProps = PhysicalPieceProps.default.copy(piece = piece,
        x = dest.x,
        y = dest.y,
        rotationDegrees = props.rotationDegrees)

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