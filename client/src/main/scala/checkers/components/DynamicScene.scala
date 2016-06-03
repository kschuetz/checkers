package checkers.components

import checkers.game.Animation.HidesStaticPiece
import checkers.game._
import checkers.geometry.Point
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object DynamicScene {

  case class Properties(playFieldState: PlayFieldState,
                        rotationDegrees: Double)


  val component = ReactComponentB[Properties]("DynamicScene")
    .render_P { props =>
      val pieceRotation = if(props.rotationDegrees != 0) -props.rotationDegrees else 0

      val piecesToHide = props.playFieldState.animations.foldLeft(Set.empty[Int]) {
        case (res, anim: HidesStaticPiece) => res + anim.hidesPieceAtSquare
        case (res, _) => res
      }

      val squares = props.playFieldState.gameState.squares



      Board.allSquares.filterNot(piecesToHide.contains).foreach { squareIndex =>
        val occupant = squares(squareIndex)
        val k = s"sp-$squareIndex"


      }

      ???

    }.build





}