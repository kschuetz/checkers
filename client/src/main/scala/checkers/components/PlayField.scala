package checkers.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object PlayField {

  //val pieces = ReactComponentB[Unit]

  //case class Properties()
  val component = ReactComponentB[Unit]("PlayField")
    .render_P { _ =>
      val physicalBoard = PhysicalBoard.Board()
      val pieces = PhysicalPiece.DefaultPieceSetup()
      <.svg.g(
        ^.svg.transform := "translate(400,400),scale(90)",
        <.svg.g(
          physicalBoard,
          pieces
        )
      )
    }.build

  def apply() = component()

}