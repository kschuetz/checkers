package checkers.components.piece

import checkers.components.BoardMouseEvent
import checkers.consts._
import checkers.geometry.Point
import checkers.util.SvgHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object PhysicalPiece extends SvgHelpers {

  val pieceRadius = 0.35
  private val pieceOverlayRadius = 0.4
  private val kingScaleAdjustment = 1.01

  protected case class RenderProps(pieceProps: PhysicalPieceProps,
                                   decoration: Decoration)

  private val Disk = ReactComponentB[(Color, Double)]("Disk")
    .render_P { case (color, radius) =>
      val classes = if (color == DARK) "disk dark" else "disk light"
      <.svg.circle(
        ^.`class` := classes,
        ^.svg.r := radius
      )
    }.build

  class PieceBodyBackend($: BackendScope[RenderProps, Unit]) {
    def render(renderProps: RenderProps) = {
      val RenderProps(props, decoration) = renderProps
      val color = COLOR(props.piece)
      val classes =
        if (color == DARK) "piece dark" else "piece light"

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

    }

  }

  private val PieceBody = ReactComponentB[RenderProps]("PieceBody")
    .renderBackend[PieceBodyBackend]
    .shouldComponentUpdateCB(_ => CallbackTo.pure(false))
    .build

  private val PieceOverlayButton = ReactComponentB[PhysicalPieceProps]("PieceOverlayButton")
    .render_P { props =>
      <.svg.circle(
        ^.classSet1("piece-button-layer", "welcome" -> props.clickable),
        ^.svg.cx := 0,
        ^.svg.cy := 0,
        ^.svg.r := pieceOverlayRadius,
        ^.onMouseDown ==>? handlePieceMouseDown(props)
      )
    }.build

  private val PieceMan = ReactComponentB[PhysicalPieceProps]("Man")
    .render_P { props =>
      val color = COLOR(props.piece)
      val baseClasses = if (color == DARK) "piece man dark" else "piece man light"
      val scale = props.scale
      <.svg.g(
        ^.classSet1(baseClasses, "ghost-piece" -> props.ghost),
//        ^.`class` := baseClasses,
        ^.svg.transform := s"translate(${props.x},${props.y}),scale($scale)",
        PieceBody(RenderProps(props, Decoration.Star)),
        PieceOverlayButton(props)
      )
    }
    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, _) =>
      val result = comparePhysicalPieceProps(scope.props, nextProps)
      CallbackTo.pure(result)
    }
    .build

  private val PieceKing = ReactComponentB[PhysicalPieceProps]("King")
    .render_P { props =>
      val color = COLOR(props.piece)
      val baseClasses = if (color == DARK) "piece king dark" else "piece king light"
      val scale = props.scale

      <.svg.g(
        ^.classSet1(baseClasses, "ghost-piece" -> props.ghost),
//        ^.`class` := classes,
        ^.svg.transform := s"translate(${props.x},${props.y}),scale($scale)",
        Disk((color, pieceRadius)),
        <.svg.g(
          ^.svg.transform := s"translate(0.07,-0.11),scale($kingScaleAdjustment)",
          PieceBody(RenderProps(props, Decoration.Crown))
        ),
        PieceOverlayButton(props)
      )
    }.build


  val component = ReactComponentB[PhysicalPieceProps]("PhysicalPiece")
    .render_P { props =>
      if (PIECETYPE(props.piece) == MAN) PieceMan(props) else PieceKing(props)
    }.build

  val apply = component


  private def handlePieceMouseDown(props: PhysicalPieceProps)(event: ReactMouseEvent): Option[Callback] = {
    val screenPoint = Point(event.clientX, event.clientY)
    val boardEvent = BoardMouseEvent(reactEvent = event,
      squareIndex = props.tag,
      onPiece = true,
      piece = props.piece,
      boardPoint = props.screenToBoard(screenPoint))
    props.callbacks.onBoardMouseDown(boardEvent)
  }

  private def comparePhysicalPieceProps(oldProps: PhysicalPieceProps, newProps: PhysicalPieceProps): Boolean = {
    oldProps != newProps
  }

}