package checkers.components

import checkers.components.board.PhysicalBoard
import checkers.components.piece._
import checkers.consts._
import checkers.core.Board
import checkers.geometry.Point
import checkers.models
import checkers.models.Animation.HidesStaticPiece
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object DynamicScene {

  //  case class Model(playField: PlayField,
  //                   rotationDegrees: Double)
  type Model = models.GameModelReader

  type Callbacks = PieceCallbacks

  type Props = (Model, Callbacks, SceneContainerContext, Point => Point)


  def testCallback(tag: Int) = Callback {
    println(s"tag $tag")
  }

//  object TestPieceEvents extends PieceCallbacks {
//    val onMouseDown = (event: PieceMouseEvent) => Some(Callback {
//      println(event)
//    })
//  }

  val component = ReactComponentB[Props]("DynamicScene")
    .render_P { case (model, callbacks, sceneContainerContext, screenToBoard) =>

      val boardRotation = model.getBoardRotation

      val pieceRotation = if(boardRotation != 0) -boardRotation else 0
      val pieceScale = 1.0d

      val piecesToHide = model.animations.foldLeft(Set.empty[Int]) {
        case (res, anim: HidesStaticPiece) => res + anim.hidesPieceAtSquare
        case (res, _) => res
      }

//      val squares = model.gameState.board.data
      val boardState = model.board

      val staticPieces = new js.Array[ReactNode]

      Board.allSquares.filterNot(piecesToHide.contains).foreach { squareIndex =>
        val occupant = boardState.getOccupant(squareIndex) //squares(squareIndex)
        if(ISPIECE(occupant)) {
            val k = s"sp-$squareIndex"

            val pos = Board.position(squareIndex)
            val pt = PhysicalBoard.positionToPoint(pos)

            val pieceProps = PhysicalPieceProps(
              piece = occupant,
              tag = squareIndex,
              x = pt.x,
              y = pt.y,
              scale = pieceScale,
              rotationDegrees = pieceRotation,
              clickable = model.clickableSquares.contains(squareIndex),
              highlighted = model.highlightedSquares.contains(squareIndex),
              screenToBoard = screenToBoard,
              callbacks = callbacks)

            val physicalPiece = PhysicalPiece.apply.withKey(k)(pieceProps)
            staticPieces.push(physicalPiece)
        }
      }

      val ghostPiece = model.ghostPiece.map { gp =>
        GhostPiece(gp)
      }

      <.svg.g(
        staticPieces,
        ghostPiece
      )

    }.build


  def apply(props: Props) = component(props)


}