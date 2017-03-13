package checkers.userinterface.board

import checkers.consts._
import checkers.core.BoardPosition
import checkers.util.Point
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{svg_<^ => svg}

import scala.scalajs.js

object PhysicalBoard {

  val squareSize: Double = 1.0
  val boardSize: Double = 8

  val squareCenterOffset: Double = squareSize / 2
  private val boardCenterOffset = 3.5 * squareSize

  def positionToPoint(position: BoardPosition): Point =
    Point(position.col - boardCenterOffset, position.row - boardCenterOffset)

  object Css {
    val dark = "dark"
    val light = "light"
    val boardSquare = "board-square"

    val darkSquare = s"$boardSquare $dark"
    val lightSquare = s"$boardSquare $light"
  }

}

class PhysicalBoard {
  import PhysicalBoard._

  private val Square = ScalaComponent.build[(Double, Double, Side)]("Square")
    .render_P { case (centerX, centerY, side) =>
      val classes =
        if (side == DARK) Css.darkSquare
        else Css.lightSquare

      svg.<.rect(
        ^.`class` := classes,
        svg.^.x := (centerX - squareCenterOffset).asInstanceOf[JsNumber],
        svg.^.y := (centerY - squareCenterOffset).asInstanceOf[JsNumber],
        svg.^.width := squareSize.asInstanceOf[JsNumber],
        svg.^.height := squareSize.asInstanceOf[JsNumber]
      )
    }.build

  private val BoardRow = ScalaComponent.build[(Double, Side)]("BoardRow")
    .render_P { case (centerY, sideOfFirst) =>
      val squares = new js.Array[VdomNode]
      (0 to 7).foldLeft(sideOfFirst) { case (side, idx) =>
        val square: VdomNode = Square.withKey(idx.toString)((idx - boardCenterOffset, 0.0, side))
        squares.push(square)
        if(side == DARK) LIGHT else DARK
      }
      svg.<.g(
        svg.^.transform := s"translate(0,$centerY)",
        squares.toVdomArray
      )
    }.build

  private val BoardBorder = ScalaComponent.build[Double]("BoardBorder")
    .render_P { thickness =>
      val origin = -4 - thickness
      val width = 8 + 2 * thickness
      svg.<.rect(
        ^.`class` := "board-border",
        svg.^.x := origin.asInstanceOf[JsNumber],
        svg.^.y := origin.asInstanceOf[JsNumber],
        svg.^.width := width.asInstanceOf[JsNumber],
        svg.^.height := width.asInstanceOf[JsNumber]
      )
    }.build

  private val Board = ScalaComponent.build[Unit]("Board")
    .render_P { _ =>
      val upperLeftSide: Side = LIGHT
      val rows = new js.Array[VdomNode]
      (0 to 7).foldLeft(upperLeftSide) { case (side, idx) =>
        val row: VdomNode = BoardRow.withKey(idx.toString)((idx - boardCenterOffset, side))
        rows.push(row)
        if(side == DARK) LIGHT else DARK
      }
      val border = BoardBorder(0.3)
      svg.<.g(
        border,
        rows.toVdomArray
      )
    }
    .shouldComponentUpdateConst(false)
    .build

  val create = Board
}