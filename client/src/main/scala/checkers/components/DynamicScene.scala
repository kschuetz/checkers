package checkers.components

import checkers.components.board.PhysicalBoard
import checkers.components.piece.{PhysicalPiece, PhysicalPieceProps, PieceEvents, PieceMouseEvent}
import checkers.game.Animation.HidesStaticPiece
import checkers.game._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object DynamicScene {

  case class Properties(playFieldState: PlayFieldState,
                        rotationDegrees: Double)


  def testCallback(tag: Int) = Callback {
    println(s"tag $tag")
  }

  object TestPieceEvents extends PieceEvents {
    val onMouseDown = (event: PieceMouseEvent) => Some(Callback {
      println(event)
    })
  }

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

            val pieceProps = PhysicalPieceProps(
              piece = piece,
              tag = squareIndex,
              x = pt.x,
              y = pt.y,
              scale = pieceScale,
              rotationDegrees = pieceRotation,
              clickable = props.playFieldState.clickableSquares.contains(squareIndex),
              highlighted = props.playFieldState.highlightedSquares.contains(squareIndex),
              events = TestPieceEvents)

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