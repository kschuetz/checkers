package checkers.userinterface

import checkers.userinterface.board.{LastMoveIndicator, PhysicalBoard, SquareOverlayButton}
import checkers.userinterface.piece._
import checkers.consts._
import checkers.core.Animation._
import checkers.core.{Board, GameModelReader, SquareAttributes}
import checkers.userinterface.animation._
import checkers.util.Point
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

object DynamicScene {

  type Model = GameModelReader

  type Callbacks = BoardCallbacks

  case class Props(gameModel: Model,
                   callbacks: Callbacks,
                   sceneContainerContext: SceneContainerContext,
                   screenToBoard: Point => Point)

}

class DynamicScene(physicalPiece: PhysicalPiece,
                   squareOverlayButton: SquareOverlayButton,
                   lastMoveIndicator: LastMoveIndicator,
                   pickedUpPiece: PickedUpPiece,
                   movingPieceAnimation: MovingPieceAnimation,
                   removingPieceAnimation: RemovingPieceAnimation,
                   jumpingPieceAnimation: JumpingPieceAnimation,
                   placingPieceAnimation: PlacingPieceAnimation,
                   crowningAnimation: CrowningAnimation,
                   illegalPieceSelectionAnimation: IllegalPieceSelectionAnimation,
                   showHintAnimation: ShowHintAnimation) {

  import DynamicScene._

  val create = ScalaComponent.build[Props]("DynamicScene")
    .render_P { case Props(model, callbacks, sceneContainerContext, screenToBoard) =>

      val boardRotation = model.getBoardRotation

      val pieceRotation = if (boardRotation != 0) -boardRotation else 0
      val pieceScale = 1.0d

      val piecesToHide = model.animation.play.foldLeft(Set.empty[Int]) {
        case (res, anim: HidesStaticPiece) => res + anim.hidesPieceAtSquare
        case (res, _) => res
      }

      val boardState = model.board

      val staticPieces = new js.Array[VdomNode]

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
            simplified = false,
            screenToBoard = screenToBoard,
            callbacks = callbacks)

          val pieceElement = physicalPiece.create.withKey(k)(pieceProps)
          staticPieces.push(pieceElement)
        }
      }

      val staticPiecesLayer = <.svg.g(
        ^.classSet("no-pointer-events" -> !model.canClickPieces),
        staticPieces
      )

      val overlayButtons = new js.Array[VdomNode]
      Board.allSquares.foreach { case (boardPos, squareIndex, pt) =>
        val k = s"s-${boardPos.row}-${boardPos.col}"
        val squareAttributes = if (squareIndex >= 0) model.squareAttributes(squareIndex)
        else SquareAttributes.default
        val occupant = if (squareIndex > 0) boardState.getOccupant(squareIndex) else EMPTY

        val props = SquareOverlayButton.Props(squareIndex, occupant, pt.x, pt.y, squareAttributes.clickable,
          screenToBoard = screenToBoard, callbacks = callbacks)
        val button = squareOverlayButton.create.withKey(k)(props)
        overlayButtons.push(button)
      }

      val pickedUpPieceElement = model.pickedUpPiece.map { p =>
        val props = PickedUpPiece.Props(p, rotationDegrees = pieceRotation)
        pickedUpPiece.create(props)
      }

      val animations = new js.Array[VdomNode]
      val nowTime = model.nowTime
      model.animation.play.foreach {
        case rp: RemovingPiece =>
          val k = s"remove-${rp.fromSquare}"
          val progress = rp.linearProgress(nowTime)
          val props = RemovingPieceAnimation.Props(rp.piece, rp.fromSquare, progress, pieceRotation)
          val component = removingPieceAnimation.create.withKey(k)(props)
          animations.push(component)

        case pp: PlacingPiece =>
          val k = s"place-${pp.toSquare}"
          val progress = pp.linearProgress(nowTime)
          val props = PlacingPieceAnimation.Props(pp.piece, pp.toSquare, progress, pieceRotation)
          val component = placingPieceAnimation.create.withKey(k)(props)
          animations.push(component)

        case mp: MovingPiece =>
          val k = s"move-${mp.fromSquare}-${mp.toSquare}"
          val progress = mp.linearProgress(nowTime)
          val props = MovingPieceAnimation.Props(mp.piece, mp.fromSquare, mp.toSquare, progress, pieceRotation)
          val component = movingPieceAnimation.create.withKey(k)(props)
          animations.push(component)

        case jp: JumpingPiece if jp.isPieceVisible(nowTime) =>
          val k = s"jump-${jp.fromSquare}-${jp.toSquare}"
          val progress = jp.linearProgress(nowTime)
          val props = JumpingPieceAnimation.Props(jp.piece, jp.fromSquare, jp.toSquare, progress, pieceRotation)
          val component = jumpingPieceAnimation.create.withKey(k)(props)
          animations.push(component)

        case cp: CrowningPiece =>
          val k = s"crown-${cp.squareIndex}"
          val progress = cp.linearProgress(nowTime)
          if (progress > 0) {
            val props = CrowningAnimation.Props(cp.side, cp.squareIndex, progress, pieceRotation)
            val component = crowningAnimation.create.withKey(k)(props)
            animations.push(component)
          }

        case ips: IllegalPieceSelection =>
          val k = s"illegal-${ips.squareIndex}"
          val progress = ips.linearProgress(nowTime)
          val props = IllegalPieceSelectionAnimation.Props(ips.piece, ips.squareIndex, progress, pieceRotation)
          val component = illegalPieceSelectionAnimation.create.withKey(k)(props)
          animations.push(component)

        case _ => ()
      }

      model.animation.hint.foreach { ha =>
        val props = ShowHintAnimation.Props(ha.fromSquare, ha.toSquare, ha.flashDuration, ha.duration, nowTime - ha.startTime)
        val component = showHintAnimation.create.withKey("hint")(props)
        animations.push(component)
      }

      val lastMoveIndicatorElement = if(model.inputPhase.waitingForMove) {
        for {
          historyEntry <- model.history.lastOption
          (fromSquare, toSquare) <- historyEntry.play.getFinalSegment
        } yield {
          val props = LastMoveIndicator.Props(fromSquare, toSquare, historyEntry.snapshot.turnToMove)
          lastMoveIndicator.create(props)
        }
      } else None

      <.svg.g(
        overlayButtons,
        lastMoveIndicatorElement,
        staticPiecesLayer,
        animations,
        pickedUpPieceElement
      )

    }.build

}