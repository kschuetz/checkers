package checkers.core

import checkers.models.Animation
import checkers.models.Animation.RemovingPiece

case class MoveAnimationPlanInput(nowTime: Double,
                                  existingAnimations: List[Animation],
                                  isComputerPlayer: Boolean,
                                  moveInfo: List[MoveInfo])

class AnimationPlanner(settings: AnimationSettings) {
  def scheduleMoveAnimations(input: MoveAnimationPlanInput): Option[List[Animation]] = {

    println(s"scheduleMoveAnimations: $input")

    def scheduleForComputer: List[Animation] = {
      // TODO
      Nil
    }

    def scheduleForHuman: List[Animation] = {
      var result = List.empty[Animation]

      def handleRemovePieces(): Unit = {
        val offset = settings.RemovePieceHumanDelayMillis
        var t = input.nowTime + offset

        input.moveInfo.foreach { moveInfo =>
          moveInfo.removedPiece.foreach { rp =>
            val animation = RemovingPiece(rp.piece, rp.squareIndex, startTime = t, delay = 0, duration = settings.RemovePieceDurationMillis)
            result = animation :: result
            t += offset
          }
        }
      }

      handleRemovePieces()

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