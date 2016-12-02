package checkers.userinterface.board

import checkers.userinterface._
import checkers.consts.Occupant
import checkers.util.Point
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object SquareOverlayButton {

  case class Props(squareIndex: Int,
                   occupant: Occupant,
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
        ^.onMouseDown ==>? handleMouseDown(props),
        ^.onMouseMove ==>? handleMouseMove(props)
      )

    }.build

  def apply(props: Props) = component(props)

  private def handleMouseDown(props: Props)(event: ReactMouseEvent): Option[Callback] = {
    val boardEvent = makeBoardEvent(props, event)
    props.callbacks.onBoardMouseDown(boardEvent)
  }

  private def handleMouseMove(props: Props)(event: ReactMouseEvent): Option[Callback] = {
    val boardEvent = makeBoardEvent(props, event)
    props.callbacks.onBoardMouseMove(boardEvent)
  }

  private def makeBoardEvent(props: Props, event: ReactMouseEvent): BoardMouseEvent = {
    val screenPoint = Point(event.clientX, event.clientY)
    val boardPoint = props.screenToBoard(screenPoint)
    val squareIndex = props.squareIndex
    BoardMouseEvent(reactEvent = event,
      squareIndex = squareIndex,
      onPiece = false,
      piece = props.occupant,
      boardPoint = boardPoint)
  }

}