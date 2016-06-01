package checkers.components

import checkers.game.{Color, Dark, Light}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactAttr
import japgolly.scalajs.react.vdom.svg.prefix_<^._


object PhysicalBoard {

  def coordinates(row: Int, col: Int): (Double, Double) =
    (col - 3.5, row - 3.5)

  def coordinatesForSquare(squareIndex: Int): (Double, Double) = {
    val (row, col) = checkers.game.Board.squareIndexToRowCol(squareIndex)
    coordinates(row, col)
  }

  object Css {
    val dark = "dark"
    val light = "light"
    val boardSquare = "board-square"

    val darkSquare = s"$boardSquare $dark"
    val lightSquare = s"$boardSquare $light"
  }

  val Square = ReactComponentB[(Double, Double, Color)]("Square")
    .render_P { case (centerX, centerY, color) =>
      val classes =  color match {
        case Dark => Css.darkSquare
        case Light => Css.lightSquare
      }

      <.rect(
        ReactAttr.ClassName := classes,
        ^.x := centerX - 0.5,
        ^.y := centerY - 0.5,
        ^.width := 1,
        ^.height := 1
      )
    }.build

  val BoardRow = ReactComponentB[(Double, Color)]("BoardRow")
    .render_P { case (centerY, colorOfFirst) =>
      val (squares, _) = (0 to 7).foldLeft((Seq.empty[ReactNode], colorOfFirst)) { case ((result, color), idx) =>
        val square: ReactNode = Square.withKey(idx)((idx - 3.5, 0.0, color))
        (result :+ square, color.opposite)
      }
      <.g(
        ^.transform := s"translate(0,$centerY)",
        squares.toJsArray
      )
    }.build

  val BoardBorder = ReactComponentB[Double]("BoardBorder")
    .render_P { thickness =>
      val origin = -4 - thickness
      val width = 8 + 2 * thickness
      <.rect(
        ReactAttr.ClassName := "board-border",
        ^.x := origin,
        ^.y := origin,
        ^.width := width,
        ^.height := width
      )
    }.build

  val Board = ReactComponentB[Unit]("Board")
    .render_P { _ =>
      val upperLeftColor: Color = Light
      val (rows, _) = (0 to 7).foldLeft((Seq.empty[ReactNode], upperLeftColor)) { case ((result, color), idx) =>
        val row: ReactNode = BoardRow.withKey(idx)((idx - 3.5, color))
        (result :+ row, color.opposite)
      }
      val border = BoardBorder(0.3)
      <.g(
        border,
        rows.toJsArray
      )
    }.build


}