package checkers.components.board

import checkers.components._
import checkers.geometry.Point
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object SquareOverlayButton {

  case class Props(squareIndex: Option[Int],
                   x: Double,
                   y: Double,
                   clickable: Boolean,
                   screenToBoard: Point => Point,
                   callbacks: BoardCallbacks)

  val component = ReactComponentB[Props]("SquareOverlayButton")
    .render_P { props =>
      <.svg.rect(
        ^.classSet1("square-overlay-button", "welcome" -> props.clickable),
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
    props.squareIndex.fold(
      props.callbacks.onBoardMouseDown(BoardMouseEvent(event, boardPoint))
    ) { square =>
      val squareEvent = SquareMouseEvent(event, square, boardPoint)
      props.callbacks.onSquareMouseDown(squareEvent)
    }
  }

}