package checkers.components.board

import checkers.components._
import checkers.geometry.Point
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object SquareOverlayButton {

  case class Props(squareIndex: Int,
                   x: Double,
                   y: Double,
                   clickable: Boolean,
                   screenToBoard: Point => Point,
                   callbacks: BoardCallbacks)

  val component = ReactComponentB[Props]("SquareOverlayButton")
    .render_P { props =>
      <.svg.rect(
        ^.classSet1("square-button-layer", "welcome" -> props.clickable),
        ^.svg.x := props.x - PhysicalBoard.squareCenterOffset,
        ^.svg.y := props.y - PhysicalBoard.squareCenterOffset,
        ^.svg.width := PhysicalBoard.squareSize,
        ^.svg.height := PhysicalBoard.squareSize,
        ^.onMouseDown ==>? handleMouseDown(props)
      )

    }.build

  def apply(props: Props) = component(props)

  private def handleMouseDown(props: Props)(event: ReactMouseEvent): Option[Callback] = {
    val screenPoint = Point(event.clientX, event.clientY)
    val boardPoint = props.screenToBoard(screenPoint)
    val squareIndex = props.squareIndex
    if(squareIndex < 0) {
      val boardEvent = BoardMouseEvent(event, boardPoint)
      props.callbacks.onBoardMouseDown(boardEvent)
    } else {
      val squareEvent = SquareMouseEvent(event, squareIndex, boardPoint)
      props.callbacks.onSquareMouseDown(squareEvent)
    }
  }

}