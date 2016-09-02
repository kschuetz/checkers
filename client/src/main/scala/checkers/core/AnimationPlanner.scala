package checkers.core

import checkers.consts.Occupant

case class MoveAnimationPlanInput(nowTime: Double,
                                  existingAnimations: List[Animation],
                                  isComputerPlayer: Boolean,
                                  moveInfo: List[MoveInfo])

case class IllegalPieceAnimationInput(nowTime: Double,
                                      existingAnimations: List[Animation],
                                      piece: Occupant,
                                      squareIndex: Int)

class AnimationPlanner(settings: AnimationSettings) {

  import Animation._

  def scheduleMoveAnimations(input: MoveAnimationPlanInput): Option[List[Animation]] = {

    println(s"scheduleMoveAnimations: $input")

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

      result
    }

    def scheduleForHuman: List[Animation] = {
      val startTime = input.nowTime
      var result = List.empty[Animation]
      // Moving pieces or jumping pieces are not animated for humans
      result = handleRemovePieces(startTime, settings.RemovePieceHumanDelayMillis, settings.RemovePieceHumanIntervalMillis, result)
      result
    }

    val newAnimations = if (input.isComputerPlayer) scheduleForComputer else scheduleForHuman

    newAnimations match {
      case Nil => None
      case anims =>
        println(s"scheduling anims: $anims")
        Some(input.existingAnimations ++ anims)
    }
  }

  def illegalPieceSelection(input: IllegalPieceAnimationInput): Option[List[Animation]] = {
    val startMovingTime = input.nowTime

    val animation = IllegalPieceSelection(input.piece, input.squareIndex, input.nowTime,
      startMovingTime, startMovingTime + settings.IllegalPieceSelectionDurationMillis)
    Some(animation :: input.existingAnimations)
  }


}