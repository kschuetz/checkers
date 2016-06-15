package checkers.components.board

import checkers.consts._
import checkers.core.BoardPosition
import checkers.geometry.Point
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object PhysicalBoard {

  val squareSize: Double = 1.0
  val boardSize: Double = 8

  private val squareCenterOffset = squareSize / 2
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

  val Square = ReactComponentB[(Double, Double, Color)]("Square")
    .render_P { case (centerX, centerY, color) =>
      val classes =
        if (color == DARK) Css.darkSquare
        else Css.lightSquare

      <.svg.rect(
        ^.`class` := classes,
        ^.svg.x := centerX - squareCenterOffset,
        ^.svg.y := centerY - squareCenterOffset,
        ^.svg.width := squareSize,
        ^.svg.height := squareSize
      )
    }.build

  val BoardRow = ReactComponentB[(Double, Color)]("BoardRow")
    .render_P { case (centerY, colorOfFirst) =>
      val squares = new js.Array[ReactNode]
      (0 to 7).foldLeft(colorOfFirst) { case (color, idx) =>
        val square: ReactNode = Square.withKey(idx)((idx - boardCenterOffset, 0.0, color))
        squares.push(square)
        if(color == DARK) LIGHT else DARK
      }
      <.svg.g(
        ^.svg.transform := s"translate(0,$centerY)",
        squares
      )
    }.build

  val BoardBorder = ReactComponentB[Double]("BoardBorder")
    .render_P { thickness =>
      val origin = -4 - thickness
      val width = 8 + 2 * thickness
      <.svg.rect(
        ^.`class` := "board-border",
        ^.svg.x := origin,
        ^.svg.y := origin,
        ^.svg.width := width,
        ^.svg.height := width
      )
    }.build

  val Board = ReactComponentB[Unit]("Board")
    .render_P { _ =>
      val upperLeftColor: Color = LIGHT
      val rows = new js.Array[ReactNode]
      (0 to 7).foldLeft(upperLeftColor) { case (color, idx) =>
        val row: ReactNode = BoardRow.withKey(idx)((idx - boardCenterOffset, color))
        rows.push(row)
        if(color == DARK) LIGHT else DARK
      }
      val border = BoardBorder(0.3)
      <.svg.g(
        border,
        rows
      )
    }.build


}