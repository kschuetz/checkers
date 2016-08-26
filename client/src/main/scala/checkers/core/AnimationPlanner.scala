package checkers.core

import checkers.models.Animation
import checkers.models.Animation.{MovingPiece, RemovingPiece}

case class MoveAnimationPlanInput(nowTime: Double,
                                  existingAnimations: List[Animation],
                                  isComputerPlayer: Boolean,
                                  moveInfo: List[MoveInfo])

class AnimationPlanner(settings: AnimationSettings) {
  def scheduleMoveAnimations(input: MoveAnimationPlanInput): Option[List[Animation]] = {

    println(s"scheduleMoveAnimations: $input")


    def handleRemovePieces(offset: Double, incoming: List[Animation]): List[Animation] = {
      var result = incoming
      var t = input.nowTime + offset

      input.moveInfo.foreach { moveInfo =>
        moveInfo.removedPiece.foreach { rp =>
          val animation = RemovingPiece(
            piece = rp.piece,
            fromSquare = rp.squareIndex,
            startTime = input.nowTime,
            startMovingTime = t,
            endTime = t + settings.RemovePieceDurationMillis)
          result = animation :: result
          t += offset
        }
      }
      result
    }

    def handleMovePieces(incoming: List[Animation]): List[Animation] = {
      var result = incoming
      val duration = settings.MovePieceDurationMillis
      var t = input.nowTime
      input.moveInfo.foreach { moveInfo =>
        if(moveInfo.isNormalMove) {
          println("SCHEDULING MOVE!")
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

    def scheduleForComputer: List[Animation] = {
      var result = List.empty[Animation]

      result = handleMovePieces(result)
      result = handleRemovePieces(settings.RemovePieceComputerDelayMillis, result)

      result
    }

    def scheduleForHuman: List[Animation] = {
      var result = List.empty[Animation]
      // Moving pieces or jumping pieces are not animated for humans
      result = handleRemovePieces(settings.RemovePieceHumanDelayMillis, result)
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


}