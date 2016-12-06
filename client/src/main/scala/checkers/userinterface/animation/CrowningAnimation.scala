package checkers.userinterface.animation

import checkers.consts._
import checkers.core.Board
import checkers.userinterface.piece.{PhysicalPiece, PhysicalPieceProps}
import checkers.util.Easing
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object CrowningAnimation {

  case class Props(side: Side,
                   square: Int,
                   progress: Double,
                   rotationDegrees: Double)

}

class CrowningAnimation(physicalPiece: PhysicalPiece,
                        animationEntryPoints: AnimationEntryPoints) {

  import CrowningAnimation._

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props): ReactElement = {
      val piece = if (props.side == DARK) DARKMAN else LIGHTMAN

      val entryPoint = animationEntryPoints.exitPoint(piece, props.square)
      val dest = Board.squareCenter(props.square)

      val t = Easing.easeInQuad(props.progress)
      val x = entryPoint.x + (dest.x - entryPoint.x) * t
      val y = entryPoint.y + (dest.y - entryPoint.y) * t

      val topProps = PhysicalPieceProps.default.copy(piece = piece,
        x = x,
        y = y,
        rotationDegrees = props.rotationDegrees)

      val topPiece = physicalPiece.create(topProps)

      val bottomProps = PhysicalPieceProps.default.copy(piece = piece,
        x = dest.x,
        y = dest.y,
        rotationDegrees = props.rotationDegrees)

      val bottomPiece = physicalPiece.create(bottomProps)

      <.svg.g(
        bottomPiece,
        topPiece
      )
    }
  }


  val create = ReactComponentB[Props]("CrowningAnimation")
    .renderBackend[Backend]
    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, _) =>
      val result = scope.props != nextProps
      CallbackTo.pure(result)
    }
    .build

}