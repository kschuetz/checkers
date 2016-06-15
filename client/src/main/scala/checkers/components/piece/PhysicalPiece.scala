package checkers.components.piece

import checkers.geometry.Point
import checkers.consts._
import checkers.core.Occupant
import checkers.util.SvgHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object PhysicalPiece extends SvgHelpers {

  val pieceRadius = 0.35
  private val pieceOverlayRadius = 0.4

  private case class RenderProps(pieceProps: PhysicalPieceProps,
                                 decoration: Decoration)

  private val Disk = ReactComponentB[(Color, Double)]("Disk")
    .render_P { case (color, radius) =>
      val classes = if(color == Dark) "disk dark" else "disk light"
      <.svg.circle(
        ^.`class` := classes,
        ^.svg.r := radius
      )
    }.build

  private val PieceBody = ReactComponentB[RenderProps]("PieceBody")
    .render_P { case RenderProps(props, decoration) =>
      val color = Occupant.colorOf(props.piece)
      val classes =
        if(color == Dark) "piece dark" else "piece light"

      val pips = new js.Array[ReactNode]
      (0 to 11).foreach { pipIndex =>
        val pt = Decorations.pipCoordinates(pipIndex)
        pips.push(Decorations.Pip.withKey(pipIndex)((color, pt)))
      }

      <.svg.g(
        ^.`class` := classes,
        (props.rotationDegrees != 0) ?= (^.svg.transform := s"rotate(${props.rotationDegrees})"),
        Disk((color, pieceRadius)),
        pips,
        Decorations.PieceDecoration((color, decoration))
      )

    }.build

  private val PieceOverlayButton = ReactComponentB[PhysicalPieceProps]("PieceOverlayButton")
    .render_P { props =>
      <.svg.circle(
        ^.`class` := "piece-button-layer",
        ^.svg.cx := 0,
        ^.svg.cy := 0,
        ^.svg.r := pieceOverlayRadius,
        ^.onMouseDown ==>? handlePieceMouseDown(props)
      )
    }.build

  private val PieceMan = ReactComponentB[PhysicalPieceProps]("Man")
    .render_P { props =>
      val color = Occupant.colorOf(props.piece)
      val classes = if(color == Dark) "man dark" else "man light"
      <.svg.g(
        ^.`class` := classes,
        ^.svg.transform := s"translate(${props.x},${props.y})",
        PieceBody(RenderProps(props, Decoration.Star)),
        props.clickable ?= PieceOverlayButton(props)
      )
    }.build

  private val PieceKing = ReactComponentB[PhysicalPieceProps]("King")
    .render_P { props =>
      val color = Occupant.colorOf(props.piece)
      val classes = if(color == Dark) "piece king dark" else "piece king light"

      <.svg.g(
        ^.`class` := classes,
        ^.svg.transform := s"translate(${props.x},${props.y})",
        Disk((color, pieceRadius)),
        <.svg.g(
          ^.svg.transform := "translate(0.07,-0.11),scale(1.01)",
          PieceBody(RenderProps(props, Decoration.Crown))
        ),
        props.clickable ?= PieceOverlayButton(props)
      )
    }.build


  val component = ReactComponentB[PhysicalPieceProps]("PhysicalPiece")
    .render_P { props =>
      if(Occupant.pieceType(props.piece) == Man) PieceMan(props) else PieceKing(props)
    }.build

  val apply = component


  private def handlePieceMouseDown(props: PhysicalPieceProps)(event: ReactMouseEvent): Option[Callback] = {
    val screenPoint = Point(event.clientX, event.clientY)
    val pieceEvent = PieceMouseEvent(event, props.piece, props.tag, props.screenToBoard(screenPoint))
    props.callbacks.onPieceMouseDown(pieceEvent)
  }

}