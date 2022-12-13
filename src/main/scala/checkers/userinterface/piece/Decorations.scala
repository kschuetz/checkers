package checkers.userinterface.piece

import checkers.consts.{DARK, Side}
import checkers.util.{Point, SvgHelpers}
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{svg_<^ => svg}

sealed trait Decoration

object Decoration {

  case object Star extends Decoration

  case object Crown extends Decoration

}

class Decorations extends SvgHelpers {

  private val pipDistanceFromEdge = 0.01
  private val pipDistanceFromCenter = PhysicalPiece.pieceRadius - pipDistanceFromEdge

  private val outerStarRadius = 0.5
  private val innerStarRadius = 0.25

  val pipCoordinates: Vector[Point] = (0 to 11).map { idx =>
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

  val Pip = ScalaComponent.builder[(Side, Point)]("Pip")
    .render_P { case (side, Point(cx, cy)) =>
      val classes = if(side == DARK) "pip dark" else "pip light"
      svg.<.circle(
        ^.`class` := classes,
        svg.^.cx := cx.asInstanceOf[JsNumber],
        svg.^.cy := cy.asInstanceOf[JsNumber],
        svg.^.r := 0.03.asInstanceOf[JsNumber]
      )
    }.build

  private val CrownPart = ScalaComponent.builder[((String, String, String), Int)]("CrownPart")
    .render_P { case ((classesA, classesB, classesC), idx) =>
      val Point(cx, cy) = topCrownPoints(idx)
      val cl1 = if (idx > 1) classesA else classesB
      svg.<.g(
        svg.<.polygon(
          ^.`class` := cl1,
          svg.^.points := crownPaths(idx)
        ),
        svg.<.circle(
          ^.`class` := classesC,
          svg.^.cx := cx.asInstanceOf[JsNumber],
          svg.^.cy := cy.asInstanceOf[JsNumber],
          svg.^.r := 0.06.asInstanceOf[JsNumber]
        )
      )
    }.build

  val Crown = ScalaComponent.builder[(Side, Double)]("Crown")
    .render_P { case (side, scale) =>
      val classes = if (side == DARK) ("crown-a dark", "crown-b dark", "crown-c dark")
      else ("crown-a light", "crown-b light", "crown-c light")
      val parts = VdomArray.empty()
      crownPaths.indices.foreach { idx =>
        parts += CrownPart.withKey(idx.toString)((classes, idx))
      }

      svg.<.g(
        ^.`class` := "crown",
        svg.^.transform := s"scale($scale)",
        parts
      )

    }.build


  val Star = ScalaComponent.builder[(Side, Double)]("Star")
    .render_P { case (side, scale) =>
      val (classesA, classesB) =
        if(side == DARK) ("star-a dark", "star-b dark")
        else ("star-a light", "star-b light")


      var k = 0
      val parts = VdomArray.empty()

      starPathA.foreach { points =>
        k += 1
        val part = svg.<.polygon(
          ^.key := k,
          ^.`class` := classesA,
          svg.^.points := points
        )
        parts += part
      }

      starPathB.foreach { points =>
        k += 1
        val part = svg.<.polygon(
          ^.key := k,
          ^.`class` := classesB,
          svg.^.points := points
        )
        parts += part
      }

      svg.<.g(
        ^.`class` := "star",
        svg.^.transform := s"scale($scale)",
        parts
      )

    }.build

  val PieceDecoration = ScalaComponent.builder[(Side, Decoration)]("PieceDecoration")
    .render_P {
      case (side, Decoration.Star) => Star((side, 0.55))
      case (side, Decoration.Crown) => Crown((side, 0.55))
    }.build


}
