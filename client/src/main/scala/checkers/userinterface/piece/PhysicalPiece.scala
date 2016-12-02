package checkers.userinterface.piece

import checkers.userinterface.BoardMouseEvent
import checkers.consts._
import checkers.util.{Point, SvgHelpers}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object PhysicalPiece extends SvgHelpers {

  val pieceRadius = 0.35
  private val pieceOverlayRadius = 0.4
  private val kingScaleAdjustment = 1.01

  protected case class RenderProps(pieceProps: PhysicalPieceProps,
                                   decoration: Decoration,
                                   translate: Option[String] = None)

  private val Disk = ReactComponentB[(Side, Double)]("Disk")
    .render_P { case (side, radius) =>
      val classes = if (side == DARK) "disk dark" else "disk light"
      <.svg.circle(
        ^.`class` := classes,
        ^.svg.r := radius
      )
    }.build

  class PieceBodyBackend($: BackendScope[RenderProps, Unit]) {
    def render(renderProps: RenderProps) = {
      val RenderProps(props, decoration, translate) = renderProps
      val side = SIDE(props.piece)
      val classes =
        if (side == DARK) "piece dark" else "piece light"

      val pips = new js.Array[ReactNode]
      (0 to 11).foreach { pipIndex =>
        val pt = Decorations.pipCoordinates(pipIndex)
        pips.push(Decorations.Pip.withKey(pipIndex)((side, pt)))
      }

      val transform = if(props.rotationDegrees != 0) {
        val rotate = s"rotate(${props.rotationDegrees})"
        translate.fold(rotate)(s => s"$rotate,$s")
      } else translate.getOrElse("")

      <.svg.g(
        ^.`class` := classes,
        transform.nonEmpty ?= (^.svg.transform := transform),
        Disk((side, pieceRadius)),
        pips,
        Decorations.PieceDecoration((side, decoration))
      )

    }

  }

  private val PieceBody = ReactComponentB[RenderProps]("PieceBody")
    .renderBackend[PieceBodyBackend]
//    .shouldComponentUpdateCB(_ => CallbackTo.pure(false))
    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, _) =>
      val result = scope.props.pieceProps.rotationDegrees != nextProps.pieceProps.rotationDegrees
      CallbackTo.pure(result)
    }
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
      val side = SIDE(props.piece)
      val baseClasses = if (side == DARK) "piece man dark" else "piece man light"
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
      val side = SIDE(props.piece)
      val baseClasses = if (side == DARK) "piece king dark" else "piece king light"
      val scale = props.scale

      val topPieceTranslate = "translate(0.07,-0.11)"

      <.svg.g(
        ^.classSet1(baseClasses, "ghost-piece" -> props.ghost),
        ^.svg.transform := s"translate(${props.x},${props.y}),scale($scale)",
        Disk((side, pieceRadius)),
        <.svg.g(
          ^.svg.transform := s"scale($kingScaleAdjustment)",
          PieceBody(RenderProps(props, Decoration.Crown, Some(topPieceTranslate)))
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