package checkers.computer

import checkers.core._
import checkers.util.Random


class ComputerPlayer(moveGenerator: MoveGenerator,
                     personality: Personality)
                    (initialSeed: Option[Long]) extends Program {
  override def initialState = {
    val random = initialSeed.fold(Random())(seed => Random.apply(seed))
    ComputerPlayerState(random)
  }

  override def play(state: Opaque, playInput: PlayInput): PlayComputation = {
    val myState = state.asInstanceOf[ComputerPlayerState]
    val (newState, searchParameters) = personality.getSearchParameters(myState, playInput)
    ???
  }


}