package checkers.components

import checkers.components.board.{PhysicalBoard, SquareOverlayButton}
import checkers.components.piece._
import checkers.consts._
import checkers.core.Board
import checkers.geometry.Point
import checkers.models
import checkers.models.Animation.{HidesStaticPiece, MovingPiece, RemovingPiece}
import checkers.models.SquareAttributes
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object DynamicScene {

  //  case class Model(playField: PlayField,
  //                   rotationDegrees: Double)
  type Model = models.GameModelReader

  type Callbacks = BoardCallbacks

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

      val pieceRotation = if (boardRotation != 0) -boardRotation else 0
      val pieceScale = 1.0d

      val piecesToHide = model.animations.foldLeft(Set.empty[Int]) {
        case (res, anim: HidesStaticPiece) => res + anim.hidesPieceAtSquare
        case (res, _) => res
      }

      val boardState = model.board

      val staticPieces = new js.Array[ReactNode]

      Board.playableSquares.filterNot(piecesToHide.contains).foreach { squareIndex =>
        val occupant = boardState.getOccupant(squareIndex) //squares(squareIndex)
        if (ISPIECE(occupant)) {
          val k = s"sp-$squareIndex"

          val pos = Board.position(squareIndex)
          val pt = PhysicalBoard.positionToPoint(pos)

          val squareAttributes = model.squareAttributes(squareIndex)

          val pieceProps = PhysicalPieceProps(
            piece = occupant,
            tag = squareIndex,
            x = pt.x,
            y = pt.y,
            scale = pieceScale,
            rotationDegrees = pieceRotation,
            clickable = squareAttributes.clickable,
            highlighted = squareAttributes.highlighted,
            ghost = squareAttributes.ghost,
            screenToBoard = screenToBoard,
            callbacks = callbacks)

          val physicalPiece = PhysicalPiece.apply.withKey(k)(pieceProps)
          staticPieces.push(physicalPiece)
        }
      }

      val staticPiecesLayer = <.svg.g(
        ^.classSet("no-pointer-events" -> !model.canClickPieces),
        staticPieces
      )

      val overlayButtons = new js.Array[ReactNode]
      Board.allSquares.foreach { case (boardPos, squareIndex, pt) =>
        val k = s"s-${boardPos.row}-${boardPos.col}"
        val squareAttributes = if(squareIndex >= 0) model.squareAttributes(squareIndex)
          else SquareAttributes.default
        val occupant = if(squareIndex > 0) boardState.getOccupant(squareIndex) else EMPTY

        val props = SquareOverlayButton.Props(squareIndex, occupant, pt.x, pt.y, squareAttributes.clickable,
          screenToBoard = screenToBoard, callbacks = callbacks)
        val button = SquareOverlayButton.component.withKey(k)(props)
        overlayButtons.push(button)
      }

      val pickedUpPiece = model.pickedUpPiece.map { gp =>
        PickedUpPiece(gp)
      }

      val animations = new js.Array[ReactNode]
      val nowTime = model.nowTime
      model.animations.zipWithIndex.foreach { case (anim, idx) =>
        val k = idx.toString
        anim match {
          case rp: RemovingPiece =>
            val progress = rp.linearProgress(nowTime)
            val props = RemovingPieceAnimation.Props(rp.piece, rp.fromSquare, progress)
            val component = RemovingPieceAnimation.component.withKey(k)(props)
            animations.push(component)

          case mp: MovingPiece =>
            val progress = mp.linearProgress(nowTime)
            val props = MovingPieceAnimation.Props(mp.piece, mp.fromSquare, mp.toSquare, progress)
            val component = MovingPieceAnimation.component.withKey(k)(props)
            animations.push(component)

          case _ => ()
        }
      }

      <.svg.g(
        overlayButtons,
        staticPiecesLayer,
        animations,
        pickedUpPiece
      )

    }.build


  def apply(props: Props) = component(props)


}