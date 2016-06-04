package checkers.components.piece

import checkers.game.{Color, Dark, Light}
import checkers.geometry.Point
import checkers.util.SvgHelpers
import japgolly.scalajs.react.{ReactComponentB, ReactNode}
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

sealed trait Decoration

object Decoration {

  case object Star extends Decoration

  case object Crown extends Decoration

}

object Decorations extends SvgHelpers {

  private val pipDistanceFromEdge = 0.01
  private val pipDistanceFromCenter = PhysicalPiece.pieceRadius - pipDistanceFromEdge

  private val outerStarRadius = 0.5
  private val innerStarRadius = 0.25

  val pipCoordinates = (0 to 11).map { idx =>
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

  val Pip = ReactComponentB[(Color, Point)]("Pip")
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

  val Crown = ReactComponentB[(Color, Double)]("Crown")
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


  val Star = ReactComponentB[(Color, Double)]("Star")
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

  val PieceDecoration = ReactComponentB[(Color, Decoration)]("PieceDecoration")
    .render_P {
      case (color, Decoration.Star) => Star((color, 0.55))
      case (color, Decoration.Crown) => Crown((color, 0.55))
    }.build


}