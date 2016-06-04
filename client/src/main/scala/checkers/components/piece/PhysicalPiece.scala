package checkers.components.piece

import checkers.game._
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
      val classes = color match {
        case Dark => "disk dark"
        case Light => "disk light"
      }
      <.svg.circle(
        ^.`class` := classes,
        ^.svg.r := radius
      )
    }.build

  private val PieceBody = ReactComponentB[RenderProps]("PieceBody")
    .render_P { case RenderProps(props, decoration) =>
      val classes = props.piece.color match {
        case Dark => "piece dark"
        case Light => "piece light"
      }

      val pips = new js.Array[ReactNode]
      (0 to 11).foreach { pipIndex =>
        val pt = Decorations.pipCoordinates(pipIndex)
        pips.push(Decorations.Pip.withKey(pipIndex)((props.piece.color, pt)))
      }

      <.svg.g(
        ^.`class` := classes,
        (props.rotationDegrees != 0) ?= (^.svg.transform := s"rotate(${props.rotationDegrees})"),
        Disk((props.piece.color, pieceRadius)),
        pips,
        Decorations.PieceDecoration((props.piece.color, decoration))
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

  private val Man = ReactComponentB[PhysicalPieceProps]("Man")
    .render_P { props =>
      val classes = props.piece.color match {
        case Dark => "man dark"
        case Light => "man light"
      }
      <.svg.g(
        ^.`class` := classes,
        ^.svg.transform := s"translate(${props.x},${props.y})",
        PieceBody(RenderProps(props, Decoration.Star)),
        props.clickable ?= PieceOverlayButton(props)
      )
    }.build

  private val King = ReactComponentB[PhysicalPieceProps]("King")
    .render_P { props =>
      val classes = props.piece.color match {
        case Dark => "piece king dark"
        case Light => "piece king light"
      }

      <.svg.g(
        ^.`class` := classes,
        ^.svg.transform := s"translate(${props.x},${props.y})",
        Disk((props.piece.color, pieceRadius)),
        <.svg.g(
          ^.svg.transform := "translate(0.07,-0.11),scale(1.01)",
          PieceBody(RenderProps(props, Decoration.Crown))
        ),
        props.clickable ?= PieceOverlayButton(props)
      )
    }.build


  val component = ReactComponentB[PhysicalPieceProps]("PhysicalPiece")
    .render_P { props =>
      props.piece.pieceType match {
        case PieceType.Man => Man(props)
        case PieceType.King => King(props)
      }
    }.build

  val apply = component


  private def handlePieceMouseDown(props: PhysicalPieceProps)(event: ReactMouseEvent): Option[Callback] = {
    val pieceEvent = PieceMouseEvent(event, props.piece, props.tag)
    props.events.onMouseDown(pieceEvent)
  }

}