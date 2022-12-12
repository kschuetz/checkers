package checkers.userinterface.animation

import checkers.consts._
import checkers.core.Board
import checkers.userinterface.piece.{PhysicalPiece, PhysicalPieceProps}
import checkers.util.{Easing, Point}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.VdomElement

object PlacingPieceAnimation {

  case class Props(piece: Occupant,
                   toSquare: Int,
                   progress: Double,
                   rotationDegrees: Double)

}

class PlacingPieceAnimation(physicalPiece: PhysicalPiece,
                            animationEntryPoints: AnimationEntryPoints) {

  import PlacingPieceAnimation._

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props): VdomElement = {
      val t = Easing.easeInQuad(props.progress)
      val ptA = animationEntryPoints.entryPoint(props.piece, props.toSquare)
      val ptB = endingPoint(props.piece, props.toSquare)

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


  val create = ScalaComponent.builder[Props]("PlacingPieceAnimation")
    .renderBackend[Backend]
    .shouldComponentUpdate { x => CallbackTo.pure(x.cmpProps(_ != _)) }
    .build

  private def endingPoint(piece: Occupant, fromSquare: Int): Point = {
    Board.squareCenter(fromSquare)
  }

}