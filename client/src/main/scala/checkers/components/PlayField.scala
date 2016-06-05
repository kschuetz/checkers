package checkers.components

import checkers.components.board.PhysicalBoard
import checkers.game.TestStates
import checkers.models
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object PlayField {

  //val pieces = ReactComponentB[Unit]

  //case class Properties()
  val component = ReactComponentB[Unit]("PlayField")
    .render_P { _ =>
      val physicalBoard = PhysicalBoard.Board()
      //val pieces = PhysicalPiece.DefaultPieceSetup()
      val dynamicSceneProps = models.DynamicScene(TestStates.playFieldState1, 0)
      val dynamicScene = DynamicScene.apply(dynamicSceneProps)
      <.svg.g(
        ^.svg.transform := "translate(400,400),scale(90)",
        <.svg.g(
          physicalBoard,
          dynamicScene
        )
      )
    }.build

  def apply() = component()

}