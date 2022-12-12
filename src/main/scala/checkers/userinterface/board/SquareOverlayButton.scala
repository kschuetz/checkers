package checkers.userinterface.board

import checkers.userinterface._
import checkers.consts.Occupant
import checkers.util.Point
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{svg_<^ => svg}

object SquareOverlayButton {

  case class Props(squareIndex: Int,
                   occupant: Occupant,
                   x: Double,
                   y: Double,
                   clickable: Boolean,
                   screenToBoard: Point => Point,
                   callbacks: BoardCallbacks)

}

class SquareOverlayButton {

  import SquareOverlayButton._

  val create = ScalaComponent.builder[Props]("SquareOverlayButton")
    .render_P { props =>
      svg.<.rect(
        ^.classSet1("square-button-layer", "welcome" -> props.clickable),
        svg.^.x := (props.x - PhysicalBoard.squareCenterOffset).asInstanceOf[JsNumber],
        svg.^.y := (props.y - PhysicalBoard.squareCenterOffset).asInstanceOf[JsNumber],
        svg.^.width := PhysicalBoard.squareSize.asInstanceOf[JsNumber],
        svg.^.height := PhysicalBoard.squareSize.asInstanceOf[JsNumber],
        ^.onMouseDown ==>? handleMouseDown(props),
        ^.onMouseMove ==>? handleMouseMove(props)
      )

    }.build

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