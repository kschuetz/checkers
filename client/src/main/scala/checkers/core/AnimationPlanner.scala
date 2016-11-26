package checkers.core

import checkers.consts._
import checkers.logger

case class MoveAnimationPlanInput(nowTime: Double,
                                  animationModel: AnimationModel,
                                  isComputerPlayer: Boolean,
                                  moveInfo: List[MoveInfo])

case class IllegalPieceAnimationInput(nowTime: Double,
                                      animationModel: AnimationModel,
                                      piece: Occupant,
                                      squareIndex: Int)

case class PlacePiecesAnimationInput(nowTime: Double,
                                     animationModel: AnimationModel,
                                     boardState: BoardStateRead)

class AnimationPlanner(settings: AnimationSettings) {

  private val log = logger.animations

  import Animation._

  def scheduleMoveAnimations(input: MoveAnimationPlanInput): Option[AnimationModel] = {

    log.debug(s"scheduleMoveAnimations: $input")

    def handleRemovePieces(startTime: Double, delay: Double, interval: Double, incoming: List[Animation]): List[Animation] = {
      var result = incoming
      var t = startTime + delay

      input.moveInfo.foreach { moveInfo =>
        moveInfo.removedPiece.foreach { rp =>
          val animation = RemovingPiece(
            piece = rp.piece,
            fromSquare = rp.squareIndex,
            startTime = startTime,
            startMovingTime = t,
            endTime = t + settings.RemovePieceDurationMillis)
          result = animation :: result
          t += interval
        }
      }
      result
    }

    def handleMovePieces(startTime: Double, incoming: List[Animation]): List[Animation] = {
      var result = incoming
      val duration = settings.MovePieceDurationMillis
      var t = startTime
      input.moveInfo.foreach { moveInfo =>
        if (moveInfo.isNormalMove) {
          val animation = MovingPiece(
            piece = moveInfo.piece,
            fromSquare = moveInfo.fromSquare,
            toSquare = moveInfo.toSquare,
            startTime = t,
            duration = duration)
          result = animation :: result
          t += duration
        }
      }

      result
    }

    def handleCrowning(startTime: Double, incoming: List[Animation], computerPlayer: Boolean): List[Animation] = {
      var result = incoming
      val moveDuration = settings.MovePieceDurationMillis
      val jumpDuration = settings.JumpPieceDurationMillis
      var t = startTime
      input.moveInfo.foreach { moveInfo =>
        if (computerPlayer) {
          if (moveInfo.isNormalMove) {
            t += moveDuration
          } else {
            t += jumpDuration
          }
        }
        if (moveInfo.crowned) {
          val animation = CrowningPiece(
            color = COLOR(moveInfo.piece),
            squareIndex = moveInfo.toSquare,
            startTime = startTime,
            startMovingTime = t,
            endTime = t + settings.CrownPieceDurationMillis)
          result = animation :: result
        }
      }

      result
    }

    def handleJumpPieces(startTime: Double, incoming: List[Animation]): List[Animation] = {
      val finalSquare = input.moveInfo.foldLeft(-1) { case (acc, moveInfo) =>
        if (moveInfo.isJump) moveInfo.toSquare
        else acc
      }

      if (finalSquare < 0) return incoming // no jumps found

      var result = incoming
      val duration = settings.JumpPieceDurationMillis

      var pathIndex = 0
      var t = startTime
      input.moveInfo.foreach { moveInfo =>
        if (moveInfo.isJump) {
          val animation = JumpingPiece(
            piece = moveInfo.piece,
            fromSquare = moveInfo.fromSquare,
            toSquare = moveInfo.toSquare,
            finalSquare = finalSquare,
            startTime = startTime,
            startMovingTime = t,
            endTime = t + duration,
            isFirst = pathIndex == 0
          )
          pathIndex += 1
          result = animation :: result
          t += duration
        }
      }

      result
    }

    def scheduleForComputer: List[Animation] = {
      val startTime = input.nowTime + settings.ComputerMoveDelayMillis

      var result = List.empty[Animation]

      result = handleMovePieces(startTime, result)
      result = handleJumpPieces(startTime, result)
      result = handleRemovePieces(startTime, settings.RemovePieceComputerDelayMillis,
        settings.RemovePieceComputerIntervalMillis, result)
      result = handleCrowning(startTime, result, computerPlayer = true)

      result
    }

    def scheduleForHuman: List[Animation] = {
      val startTime = input.nowTime
      var result = List.empty[Animation]
      // Moving pieces or jumping pieces are not animated for humans
      result = handleRemovePieces(startTime, settings.RemovePieceHumanDelayMillis, settings.RemovePieceHumanIntervalMillis, result)
      result = handleCrowning(startTime, result, computerPlayer = false)
      result
    }

    val newAnimations = if (input.isComputerPlayer) scheduleForComputer else scheduleForHuman

    newAnimations match {
      case Nil => None
      case anims =>
        log.debug(s"scheduling anims: $anims")
        Some(input.animationModel.addPlayAnims(anims))
    }
  }

  def illegalPieceSelection(input: IllegalPieceAnimationInput): Option[AnimationModel] = {
    val startMovingTime = input.nowTime

    val animation = IllegalPieceSelection(input.piece, input.squareIndex, input.nowTime,
      startMovingTime, startMovingTime + settings.IllegalPieceSelectionDurationMillis)
    Some(input.animationModel.addPlayAnim(animation))
  }

  def placeAllPieces(input: PlacePiecesAnimationInput): Option[AnimationModel] = {
    val interval = settings.PlacePiecesIntervalMillis
    val duration = settings.PlacePieceDurationMillis
    val boardState = input.boardState
    var newAnimations = List.empty[Animation]

    def handleSquare(square: Int, offset: Double): Double = {
      val piece = boardState.getOccupant(square)
      if (ISPIECE(piece)) {
        val anim = PlacingPiece(piece = piece, toSquare = square, startTime = input.nowTime,
          startMovingTime = offset, endTime = offset + duration)
        newAnimations = anim :: newAnimations
        offset + interval
      } else offset
    }

    var i = 0
    var bottomOffset = input.nowTime + settings.PlacePiecesBottomDelayMillis
    var topOffset = input.nowTime + settings.PlacePiecesTopDelayMillis
    while (i < 16) {
      bottomOffset = handleSquare(AnimationPlanner.bottomPlacementOrder(i), bottomOffset)
      topOffset = handleSquare(AnimationPlanner.topPlacementOrder(i), topOffset)

      i += 1
    }

    newAnimations match {
      case Nil => None
      case anims =>
        log.debug(s"scheduling placement anims: $anims")
        Some(input.animationModel.addPlayAnims(anims))
    }

  }

  def createBoardRotateAnimation(nowTime: Double): RotatingBoardAnimation = {
    RotatingBoardAnimation(nowTime, settings.BoardRotateDurationMillis)
  }

}

object AnimationPlanner {
  val bottomPlacementOrder = Vector(12, 13, 14, 15, 8, 9, 10, 11, 4, 5, 6, 7, 0, 1, 2, 3)
  val topPlacementOrder = Vector(19, 18, 17, 16, 23, 22, 21, 20, 27, 26, 25, 24, 31, 30, 29, 28)
}