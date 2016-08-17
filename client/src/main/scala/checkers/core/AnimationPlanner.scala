package checkers.core

import checkers.models.Animation

case class MoveAnimationPlanInput(existingAnimations: List[Animation],
                                  isComputerPlayer: Boolean,
                                  moveInfo: List[MoveInfo])

class AnimationPlanner {
  def scheduleMoveAnimations(input: MoveAnimationPlanInput): Option[List[Animation]] = {
    None
  }

}