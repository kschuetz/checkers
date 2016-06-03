package checkers.components

import checkers.game.Animation.HidesStaticPiece
import checkers.game._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object DynamicScene {

  case class Properties(playFieldState: PlayFieldState,
                        rotationDegrees: Double)


  val component = ReactComponentB[Properties]("DynamicScene")
    .render_P { props =>
      val pieceRotation = if(props.rotationDegrees != 0) -props.rotationDegrees else 0
      val pieceScale = 1.0d

      val piecesToHide = props.playFieldState.animations.foldLeft(Set.empty[Int]) {
        case (res, anim: HidesStaticPiece) => res + anim.hidesPieceAtSquare
        case (res, _) => res
      }

      val squares = props.playFieldState.gameState.squares

      val staticPieces = new js.Array[ReactNode]

      Board.allSquares.filterNot(piecesToHide.contains).foreach { squareIndex =>
        val occupant = squares(squareIndex)
        occupant match {
          case piece: Piece =>
            val k = s"sp-$squareIndex"

            val pos = Board.position(squareIndex)
            val pt = PhysicalBoard.positionToPoint(pos)

            val pieceProps = PhysicalPiece.Properties(piece, pt.x, pt.y, pieceRotation, pieceScale)

            val physicalPiece = PhysicalPiece.apply.withKey(k)(pieceProps)
            staticPieces.push(physicalPiece)
          case _ =>
        }
      }

      <.svg.g(
        staticPieces
      )

    }.build


    val apply = component


}