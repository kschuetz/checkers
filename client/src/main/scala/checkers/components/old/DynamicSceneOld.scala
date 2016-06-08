package checkers.components.old

import checkers.components.board.PhysicalBoard
import checkers.components.piece.{PhysicalPiece, PhysicalPieceProps, PieceCallbacks, PieceMouseEvent}
import checkers.game._
import checkers.models
import checkers.models.Animation.HidesStaticPiece
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object DynamicSceneOld {

//  case class Model(playField: PlayField,
//                   rotationDegrees: Double)
  type Model = models.DynamicScene

  def testCallback(tag: Int) = Callback {
    println(s"tag $tag")
  }

  object TestPieceEvents extends PieceCallbacks {
    val onMouseDown = (event: PieceMouseEvent) => Some(Callback {
      println(event)
    })
  }

  val component = ReactComponentB[Model]("DynamicScene")
    .render_P { props =>
      val pieceRotation = if(props.rotationDegrees != 0) -props.rotationDegrees else 0
      val pieceScale = 1.0d

      val piecesToHide = props.playField.animations.foldLeft(Set.empty[Int]) {
        case (res, anim: HidesStaticPiece) => res + anim.hidesPieceAtSquare
        case (res, _) => res
      }

      val squares = Vector.empty[Occupant] //props.playField.gameState.board.data

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
              clickable = props.playField.clickableSquares.contains(squareIndex),
              highlighted = props.playField.highlightedSquares.contains(squareIndex),
              screenToBoard = identity,
              callbacks = TestPieceEvents)

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