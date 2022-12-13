package checkers.userinterface.piece

import checkers.consts._
import checkers.userinterface.BoardMouseEvent
import checkers.util.{Point, SvgHelpers}
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{svg_<^ => svg}

object PhysicalPiece extends SvgHelpers {

  val pieceRadius = 0.35
  private val pieceOverlayRadius = 0.4
  private val kingScaleAdjustment = 1.01

  protected case class RenderProps(pieceProps: PhysicalPieceProps,
                                   decoration: Decoration,
                                   translate: Option[String] = None)

}

class PhysicalPiece(decorations: Decorations) {

  import PhysicalPiece._

  private val Disk = ScalaComponent.builder[(Side, Double)]("Disk")
    .render_P { case (side, radius) =>
      val classes = if (side == DARK) "disk dark" else "disk light"
      svg.<.circle(
        ^.`class` := classes,
        svg.^.r := radius.asInstanceOf[JsNumber]
      )
    }.build

  class PieceBodyBackend($: BackendScope[RenderProps, Unit]) {
    def render(renderProps: RenderProps): VdomElement = {
      val RenderProps(props, decoration, translate) = renderProps
      val side = SIDE(props.piece)
      val classes =
        if (side == DARK) "piece dark" else "piece light"

      val showPips = !props.simplified

      val pips = VdomArray.empty()
      if (showPips) {
        (0 to 11).foreach { pipIndex =>
          val pt = decorations.pipCoordinates(pipIndex)
          pips += decorations.Pip.withKey(pipIndex.toString)((side, pt))
        }
      }

      val transform = if (props.rotationDegrees != 0) {
        val rotate = s"rotate(${props.rotationDegrees})"
        translate.fold(rotate)(s => s"$rotate,$s")
      } else translate.getOrElse("")

      val disk = Disk((side, pieceRadius))

      svg.<.g(
        ^.`class` := classes,
        (svg.^.transform := transform).when(transform.nonEmpty),
        disk,
        pips,
        decorations.PieceDecoration((side, decoration))
      )

    }

  }

  private val PieceBody = ScalaComponent.builder[RenderProps]("PieceBody")
    .renderBackend[PieceBodyBackend]
    .shouldComponentUpdate(x => CallbackTo.pure(x.cmpProps(_.pieceProps.rotationDegrees != _.pieceProps.rotationDegrees)))
    .build

  private val PieceOverlayButton = ScalaComponent.builder[PhysicalPieceProps]("PieceOverlayButton")
    .render_P { props =>
      svg.<.circle(
        ^.classSet1("piece-button-layer", "welcome" -> props.clickable),
        svg.^.cx := 0.asInstanceOf[JsNumber],
        svg.^.cy := 0.asInstanceOf[JsNumber],
        svg.^.r := pieceOverlayRadius.asInstanceOf[JsNumber],
        ^.onMouseDown ==>? handlePieceMouseDown(props)
      )
    }.build

  private val PieceMan = ScalaComponent.builder[PhysicalPieceProps]("Man")
    .render_P { props =>
      val side = SIDE(props.piece)
      val baseClasses = if (side == DARK) "piece man dark" else "piece man light"
      val scale = props.scale
      svg.<.g(
        ^.classSet1(baseClasses, "ghost-piece" -> props.ghost),
        //        ^.`class` := baseClasses,
        svg.^.transform := s"translate(${props.x},${props.y}),scale($scale)",
        PieceBody(RenderProps(props, Decoration.Star)),
        PieceOverlayButton(props)
      )
    }
    .shouldComponentUpdate { x =>
      val result = comparePhysicalPieceProps(x.currentProps, x.nextProps)
      CallbackTo.pure(result)
    }
    .build

  private val PieceKing = ScalaComponent.builder[PhysicalPieceProps]("King")
    .render_P { props =>
      val side = SIDE(props.piece)
      val baseClasses = if (side == DARK) "piece king dark" else "piece king light"
      val scale = props.scale

      val topPieceTranslate = "translate(0.07,-0.11)"

      svg.<.g(
        ^.classSet1(baseClasses, "ghost-piece" -> props.ghost),
        svg.^.transform := s"translate(${props.x},${props.y}),scale($scale)",
        Disk((side, pieceRadius)),
        svg.<.g(
          svg.^.transform := s"scale($kingScaleAdjustment)",
          PieceBody(RenderProps(props, Decoration.Crown, Some(topPieceTranslate)))
        ),
        PieceOverlayButton(props)
      )
    }.build


  val create = ScalaComponent.builder[PhysicalPieceProps]("PhysicalPiece")
    .render_P { props =>
      if (PIECETYPE(props.piece) == MAN) PieceMan(props) else PieceKing(props)
    }.build

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
