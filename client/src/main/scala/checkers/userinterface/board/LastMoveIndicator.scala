package checkers.userinterface.board

import checkers.consts._
import checkers.core.Board
import checkers.userinterface.widgets.DirectedArrow
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.VdomElement

object LastMoveIndicator {
  case class Props(fromSquare: Int,
                   toSquare: Int,
                   side: Side)

  private val baseClasses = Map("last-move-indicator" -> true)

  private val classesDark = baseClasses + ("dark" -> true)

  private val classesLight = baseClasses + ("light" -> true)
}


class LastMoveIndicator(directedArrow: DirectedArrow) {

  import LastMoveIndicator._

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props): VdomElement = {
      val classes = if(props.side == DARK) classesDark else classesLight

      val ptA = Board.squareCenter(props.fromSquare)
      val ptB = Board.squareCenter(props.toSquare)
      val arrowProps = DirectedArrow.Props(
        source = ptA, dest = ptB, headLength = 0.4, headWidth = 0.6, baseWidth = 0.2,
        sourceMargin = 0, destMargin = 0.3, classes)

      directedArrow.create(arrowProps)
    }
  }

  val create = ScalaComponent.build[Props]("LastMoveIndicator")
    .renderBackend[Backend]
    .build
}