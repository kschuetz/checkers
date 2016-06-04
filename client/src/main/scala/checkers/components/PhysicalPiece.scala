package checkers.components

import checkers.game._
import checkers.geometry.Point
import checkers.util.SvgHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object PhysicalPiece extends SvgHelpers {

  private val pieceRadius = 0.35
  private val pieceOverlayRadius = 0.4
  private val pipDistanceFromEdge = 0.01
  private val pipDistanceFromCenter = pieceRadius - pipDistanceFromEdge

  private val outerStarRadius = 0.5
  private val innerStarRadius = 0.25


  private val pipCoordinates = (0 to 11).map { idx =>
    val theta = idx * math.Pi / 6
    val x = pipDistanceFromCenter * math.sin(theta)
    val y = pipDistanceFromCenter * math.cos(theta)
    Point(x, y)
  }.toVector

  private val (starPathA, starPathB) = {
    val outerPoints = (0 to 4).map { idx =>
      val theta = 2 * idx * math.Pi / 5
      val x = outerStarRadius * math.sin(theta)
      val y = -outerStarRadius * math.cos(theta)
      Point(x, y)
    }.toVector

    val innerPoints = (0 to 4).map { idx =>
      val theta = (2 * idx + 1) * math.Pi / 5
      val x = innerStarRadius * math.sin(theta)
      val y = -innerStarRadius * math.cos(theta)
      Point(x, y)
    }.toVector

    val aPoints = (0 to 4).map { idx =>
      val outer = outerPoints(idx)
      val inner = innerPoints(idx)
      pointsToPathString(Point.origin, inner, outer)
    }

    val bPoints = (0 to 4).map { idx =>
      val next = (idx + 1) % 5
      val inner = innerPoints(idx)
      val outer = outerPoints(next)
      pointsToPathString(Point.origin, inner, outer)
    }

    (aPoints, bPoints)
  }

  private val topCrownPoints: Vector[Point] = Vector(
    Point(-0.4, -0.12),
    Point(-0.23, -0.28),
    Point(0.0, -0.32),
    Point(0.23, -0.28),
    Point(0.4, -0.12))

  private val bottomCrownPoints: Vector[Point] = Vector(
    Point(-0.3, 0.3),
    Point(-0.24, 0.3),
    Point(0.0, 0.3),
    Point(0.24, 0.3),
    Point(0.3, 0.3))


  private val crownPaths: Vector[String] = {
    Vector((0, 1, 4), (0, 3, 4), (0, 0, 4), (1, 2, 3), (0, 4, 4)).map { case (a, b, c) =>
      pointsToPathString(bottomCrownPoints(a), topCrownPoints(b), bottomCrownPoints(c))
    }
  }

  sealed trait Decoration

  object Decoration {

    case object Star extends Decoration

    case object Crown extends Decoration

  }

  case class PieceMouseEvent(event: ReactMouseEvent,
                             piece: Piece,
                             tag: Int)

  trait PieceEvents {
    def onMouseDown: PieceMouseEvent => Option[Callback]
  }

  object EmptyPieceEvents extends PieceEvents {
    override val onMouseDown: (PieceMouseEvent) => Option[Callback] = _ => None
  }


  case class Props(piece: Piece,
                   tag: Int, // for events
                   x: Double,
                   y: Double,
                   scale: Double,
                   rotationDegrees: Double,
                   clickable: Boolean,
                   highlighted: Boolean,
                   events: PieceEvents)
//                   onMouseDown: Option[Int => Callback])
//                   onMouseDown: Option[CallbackTo[Int]])

  case class RenderProps(pieceProps: Props,
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


  private val Pip = ReactComponentB[(Color, Point)]("Pip")
    .render_P { case (color, Point(cx, cy)) =>
      val classes = color match {
        case Dark => "pip dark"
        case Light => "pip light"
      }
      <.svg.circle(
        ^.`class` := classes,
        ^.svg.cx := cx,
        ^.svg.cy := cy,
        ^.svg.r := 0.03
      )
    }.build

  private val CrownPart = ReactComponentB[((String, String, String), Int)]("CrownPart")
    .render_P { case ((classesA, classesB, classesC), idx) =>
      val Point(cx, cy) = topCrownPoints(idx)
      val cl1 = if (idx > 1) classesA else classesB
      <.svg.g(
        <.svg.polygon(
          ^.`class` := cl1,
          ^.svg.points := crownPaths(idx)
        ),
        <.svg.circle(
          ^.`class` := classesC,
          ^.svg.cx := cx,
          ^.svg.cy := cy,
          ^.svg.r := 0.06
        )
      )
    }.build

  private val Crown = ReactComponentB[(Color, Double)]("Crown")
    .render_P { case (color, scale) =>
      val classes = color match {
        case Dark => ("crown-a dark", "crown-b dark", "crown-c dark")
        case Light => ("crown-a light", "crown-b light", "crown-c light")
      }
      val parts = new js.Array[ReactNode]
      crownPaths.indices.foreach { idx =>
        parts.push(CrownPart.withKey(idx)((classes, idx)))
      }

      <.svg.g(
        ^.`class` := "crown",
        ^.svg.transform := s"scale($scale)",
        parts
      )

    }.build


  private val Star = ReactComponentB[(Color, Double)]("Star")
    .render_P { case (color, scale) =>
      val (classesA, classesB) = color match {
        case Dark => ("star-a dark", "star-b dark")
        case Light => ("star-a light", "star-b light")
      }

      var k = 0
      val parts = new js.Array[ReactNode]

      starPathA.foreach { points =>
        k += 1
        val part = <.svg.polygon(
          ^.key := k,
          ^.`class` := classesA,
          ^.svg.points := points
        )
        parts.push(part)
      }

      starPathB.foreach { points =>
        k += 1
        val part = <.svg.polygon(
          ^.key := k,
          ^.`class` := classesB,
          ^.svg.points := points
        )
        parts.push(part)
      }

      <.svg.g(
        ^.`class` := "star",
        ^.svg.transform := s"scale($scale)",
        parts
      )

    }.build

  private val PieceDecoration = ReactComponentB[(Color, Decoration)]("PieceDecoration")
    .render_P {
      case (color, Decoration.Star) => Star((color, 0.55))
      case (color, Decoration.Crown) => Crown((color, 0.55))
    }.build

  private val PieceBody = ReactComponentB[RenderProps]("PieceBody")
    .render_P { case RenderProps(props, decoration) =>
      val classes = props.piece.color match {
        case Dark => "piece dark"
        case Light => "piece light"
      }

      val pips = new js.Array[ReactNode]
      (0 to 11).foreach { pipIndex =>
        val pt = pipCoordinates(pipIndex)
        pips.push(Pip.withKey(pipIndex)((props.piece.color, pt)))
      }

      <.svg.g(
        ^.`class` := classes,
        (props.rotationDegrees != 0) ?= (^.svg.transform := s"rotate(${props.rotationDegrees})"),
        Disk((props.piece.color, pieceRadius)),
        pips,
        PieceDecoration((props.piece.color, decoration))
      )

    }.build

  private def handleMouseDown(event: ReactMouseEvent): Callback = Callback {
    println((event.clientX, event.clientY))
    //js.Dynamic.global.console.log(event)
  }

  private def handleMouseDown2(tag: Int, cb: Int => Callback)(event: ReactMouseEvent): Callback = {
    Callback { println((event.clientX, event.clientY)) } >> cb(tag)
  }

  private def handleMouseDown3(props: Props)(event: ReactMouseEvent): Option[Callback] = {
    val pieceEvent = PieceMouseEvent(event, props.piece, props.tag)
    props.events.onMouseDown(pieceEvent)
  }

  private val PieceOverlayButton = ReactComponentB[Props]("PieceOverlayButton")
    .render_P { props =>
      <.svg.circle(
        ^.`class` := "piece-button-layer",
        ^.svg.cx := 0,
        ^.svg.cy := 0,
        ^.svg.r := pieceOverlayRadius,
        ^.onMouseDown ==>? handleMouseDown3(props)
        //^.onMouseDown ==> handleMouseDown
//        props.onMouseDown.map { cb =>
//          ^.onMouseDown ==> handleMouseDown2(props.tag, cb)
//        } getOrElse EmptyTag
      )
    }.build

  private val Man = ReactComponentB[Props]("Man")
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

  private val King = ReactComponentB[Props]("King")
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


  val component = ReactComponentB[Props]("PhysicalPiece")
    .render_P { props =>
      props.piece.pieceType match {
        case PieceType.Man => Man(props)
        case PieceType.King => King(props)
      }
    }.build

  val apply = component


//  val DefaultPieceSetup = ReactComponentB[Unit]("DefaultPieceSetup")
//    .render_P { _ =>
//      val lights = Board.lightStartingSquares.map { idx =>
//        val Point(x, y) = PhysicalBoard.positionToPoint(Board.position(idx))
//        val piece = if (idx < 4) LightKing else LightMan
//        val props = Props(piece, idx, x, y, 1, 53, clickable=false, highlighted=false, onMouseDown = None)
//        apply(props)
//      }.toJsArray
//
//      val darks = Board.darkStartingSquares.map { idx =>
//        val Point(x, y) = PhysicalBoard.positionToPoint(Board.position(idx))
//        val piece = if (idx > 27) DarkKing else DarkMan
//        val props = Props(piece, idx, x, y, 1, 0, clickable=false, highlighted=false, onMouseDown = None)
//        apply(props)
//      }.toJsArray
//
//      <.svg.g(
//        lights, darks
//      )
//
//    }.build

}