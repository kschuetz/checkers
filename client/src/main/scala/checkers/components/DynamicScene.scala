package checkers.components

import checkers.components.board.{PhysicalBoard, SquareOverlayButton}
import checkers.components.piece._
import checkers.consts._
import checkers.core.Animation._
import checkers.core.{Board, GameModelReader, SquareAttributes}
import checkers.geometry.Point
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object DynamicScene {

  type Model = GameModelReader

  type Callbacks = BoardCallbacks

  case class Props(gameModel: Model,
                   callbacks: Callbacks,
                   sceneContainerContext: SceneContainerContext,
                   screenToBoard: Point => Point)


  def testCallback(tag: Int) = Callback {
    println(s"tag $tag")
  }

  //  object TestPieceEvents extends PieceCallbacks {
  //    val onMouseDown = (event: PieceMouseEvent) => Some(Callback {
  //      println(event)
  //    })
  //  }

  val component = ReactComponentB[Props]("DynamicScene")
    .render_P { case Props(model, callbacks, sceneContainerContext, screenToBoard) =>

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
        val occupant = boardState.getOccupant(squareIndex)
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
      model.animations.foreach {
          case rp: RemovingPiece =>
            val k = s"remove-${rp.fromSquare}"
            val progress = rp.linearProgress(nowTime)
            val props = RemovingPieceAnimation.Props(rp.piece, rp.fromSquare, progress)
            val component = RemovingPieceAnimation.component.withKey(k)(props)
            animations.push(component)

          case pp: PlacingPiece =>
            val k = s"place-${pp.toSquare}"
            val progress = pp.linearProgress(nowTime)
            val props = PlacingPieceAnimation.Props(pp.piece, pp.toSquare, progress)
            val component = PlacingPieceAnimation.component.withKey(k)(props)
            animations.push(component)

          case mp: MovingPiece =>
            val k = s"move-${mp.fromSquare}-${mp.toSquare}"
            val progress = mp.linearProgress(nowTime)
            val props = MovingPieceAnimation.Props(mp.piece, mp.fromSquare, mp.toSquare, progress)
            val component = MovingPieceAnimation.component.withKey(k)(props)
            animations.push(component)

          case jp: JumpingPiece if jp.isPieceVisible(nowTime) =>
            val k = s"jump-${jp.fromSquare}-${jp.toSquare}"
            val progress = jp.linearProgress(nowTime)
            val props = JumpingPieceAnimation.Props(jp.piece, jp.fromSquare, jp.toSquare, progress)
            val component = JumpingPieceAnimation.component.withKey(k)(props)
            animations.push(component)

          case ips: IllegalPieceSelection =>
            val k = s"illegal-${ips.squareIndex}"
            val progress = ips.linearProgress(nowTime)
            val props = IllegalPieceSelectionAnimation.Props(ips.piece, ips.squareIndex, progress)
            val component = IllegalPieceSelectionAnimation.component.withKey(k)(props)
            animations.push(component)

          case _ => ()
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