package checkers.components

import checkers.game.{Board, Color, Dark, Light}
import checkers.util.SvgHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactAttr
import japgolly.scalajs.react.vdom.svg.prefix_<^._

object PhysicalPiece extends SvgHelpers {

  private val pieceRadius = 0.35
  private val pipDistanceFromEdge = 0.01
  private val pipDistanceFromCenter = pieceRadius - pipDistanceFromEdge

  private val outerStarRadius = 0.5
  private val innerStarRadius = 0.25


  private val pipCoordinates = (0 to 11).map { idx =>
    val theta = idx * math.Pi / 6
    val x = pipDistanceFromCenter * math.sin(theta)
    val y = pipDistanceFromCenter * math.cos(theta)
    (x, y)
  }.toVector

  private val (starPathA, starPathB) = {
    val outerPoints = (0 to 4).map { idx =>
      val theta = 2 * idx * math.Pi / 5
      val x = outerStarRadius * math.sin(theta)
      val y = -outerStarRadius * math.cos(theta)
      (x, y)
    }.toVector

    val innerPoints = (0 to 4).map { idx =>
      val theta = (2 * idx + 1) * math.Pi / 5
      val x = innerStarRadius * math.sin(theta)
      val y = -innerStarRadius * math.cos(theta)
      (x, y)
    }.toVector

    val aPoints = (0 to 4).map { idx =>
      val outer = outerPoints(idx)
      val inner = innerPoints(idx)
      pointsToPathString((0, 0), inner, outer)
    }

    val bPoints = (0 to 4).map { idx =>
      val next = (idx + 1) % 5
      val inner = innerPoints(idx)
      val outer = outerPoints(next)
      pointsToPathString((0, 0), inner, outer)
    }

    (aPoints, bPoints)
  }

  private val topCrownPoints: Vector[(Double, Double)] = Vector(
    (-0.4, -0.12),
    (-0.23, -0.28),
    (0.0, -0.32),
    (0.23, -0.28),
    (0.4, -0.12))

  private val bottomCrownPoints: Vector[(Double, Double)] = Vector(
    (-0.3, 0.3),
    (-0.24, 0.3),
    (0.0, 0.3),
    (0.24, 0.3),
    (0.3, 0.3))


  private val crownPaths: Vector[String] = {
    Vector((0, 1, 4), (0, 3, 4), (0, 0, 4), (1, 2, 3),  (0, 4, 4)).map { case (a, b, c) =>
      pointsToPathString(bottomCrownPoints(a), topCrownPoints(b), bottomCrownPoints(c))
    }
  }

  sealed trait Decoration
  object Decoration {
    case object Star extends Decoration
    case object Crown extends Decoration
  }


  case class Properties(color: Color,
                        x: Double = 0.0,
                        y: Double = 0.0,
                        scale: Double = 1.0) {
    def toRenderProps(decoration: Decoration) = RenderProps(color, x, y, scale, decoration)
  }

  case class RenderProps(color: Color,
                         x: Double,
                         y: Double,
                         scale: Double,
                         decoration: Decoration)



  private val Disk = ReactComponentB[(Color, Double)]("Disk")
    .render_P { case (color, radius) =>
      val classes = color match {
        case Dark => "disk dark"
        case Light => "disk light"
      }
      <.circle(
        ReactAttr.ClassName := classes,
        ^.r := radius
      )
    }.build


  private val Pip = ReactComponentB[(Color, Double, Double)]("Pip")
    .render_P { case (color, cx, cy) =>
      val classes = color match {
        case Dark => "pip dark"
        case Light => "pip light"
      }
      <.circle(
        ReactAttr.ClassName := classes,
        ^.cx := cx,
        ^.cy := cy,
        ^.r := 0.03
      )
    }.build

  private val CrownPart = ReactComponentB[((String, String, String), Int)]("CrownPart")
    .render_P { case ((classesA, classesB, classesC), idx) =>
      val (cx, cy) = topCrownPoints(idx)
      val cl1 = if(idx > 1) classesA else classesB
      <.g(
        <.polygon(
          ReactAttr.ClassName := cl1,
          ^.points := crownPaths(idx)
        ),
        <.circle(
          ReactAttr.ClassName := classesC,
          ^.cx := cx,
          ^.cy := cy,
          ^.r := 0.06
        )
      )
    }.build

  private val Crown = ReactComponentB[(Color, Double)]("Crown")
    .render_P { case (color, scale) =>
      val classes = color match {
        case Dark => ("crown-a dark", "crown-b dark", "crown-c dark")
        case Light => ("crown-a light", "crown-b light", "crown-c light")
      }
      val parts = crownPaths.indices.map { idx =>
        CrownPart.withKey(idx)((classes, idx))
      }.toJsArray

      <.g(
        ReactAttr.ClassName := "crown",
        ^.transform := s"scale($scale)",
        parts
      )

    }.build


  private val Star = ReactComponentB[(Color, Double)]("Star")
    .render_P { case (color, scale) =>
      val (classesA, classesB) = color match {
        case Dark => ("star-a dark", "star-b dark")
        case Light => ("star-a light", "star-b light")
      }

      val starA = starPathA.map { points =>
        <.polygon(
          ReactAttr.ClassName := classesA,
          ^.points := points
        )
      }.toJsArray

      val starB = starPathB.map { points =>
        <.polygon(
          ReactAttr.ClassName := classesB,
          ^.points := points
        )
      }.toJsArray

      <.g(
        ReactAttr.ClassName := "star",
        ^.transform := s"scale($scale)",
        starA,
        starB
      )

    }.build

  private val PieceDecoration = ReactComponentB[(Color, Decoration)]("PieceDecoration")
    .render_P {
      case (color, Decoration.Star) => Star((color, 0.55))
      case (color, Decoration.Crown) => Crown((color, 0.55))
    }.build

  private val Piece = ReactComponentB[RenderProps]("Man")
    .render_P { props =>
      val classes = props.color match {
        case Dark => "piece dark"
        case Light => "piece light"
      }

      val pips = (0 to 11).map { pipIndex =>
        val (x, y) = pipCoordinates(pipIndex)
        Pip.withKey(pipIndex)((props.color, x, y))
      }.toJsArray

      <.g(
        ReactAttr.ClassName := classes,
        Disk((props.color, pieceRadius)),
        pips,
        PieceDecoration((props.color, props.decoration))
      )

    }.build

  val Man = ReactComponentB[Properties]("Man")
    .render_P { props =>
      val classes = props.color match {
        case Dark => "man dark"
        case Light => "man light"
      }
      <.g(
        ReactAttr.ClassName := classes,
        ^.transform := s"translate(${props.x},${props.y})",
        Piece(props.toRenderProps(Decoration.Star))
      )
    }.build

  val King = ReactComponentB[Properties]("King")
    .render_P { props =>
      val classes = props.color match {
        case Dark => "piece king dark"
        case Light => "piece king light"
      }

      <.g(
        ReactAttr.ClassName := classes,
        ^.transform := s"translate(${props.x},${props.y})",
        Disk((props.color, pieceRadius)),
        <.g(
          //ReactAttr.ClassName := classes,
          ^.transform := "translate(0.07,-0.11),scale(1.01)",
          Piece(props.toRenderProps(Decoration.Crown))
        )
      )
    }.build


  val DefaultPieceSetup = ReactComponentB[Unit]("DefaultPieceSetup")
    .render_P { _ =>
      val lights = Board.lightStartingSquares.map { idx =>
        val (x, y) = PhysicalBoard.coordinatesForSquare(idx)
        val props = Properties(Light, x, y)
        if (idx < 4) King(props) else Man(props)
      }.toJsArray

      val darks = Board.darkStartingSquares.map { idx =>
        val (x, y) = PhysicalBoard.coordinatesForSquare(idx)
        val props = Properties(Dark, x, y)
        if (idx > 27) King(props) else Man(props)
      }.toJsArray

      <.g(
        lights, darks
      )

    }.build

}