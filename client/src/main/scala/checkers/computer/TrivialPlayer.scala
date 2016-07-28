package checkers.computer

import checkers.computer.TrivialPlayer._
import checkers.core.{BoardStack, MoveDecoder, MoveGenerator, Play}
import checkers.util.Random

object TrivialPlayer {
  type State = Random
}

class TrivialPlayer(moveGenerator: MoveGenerator)
                   (initialSeed: Option[Long]) extends Program[State] {

  // Can be shared between all computations
  private val moveDecoder = new MoveDecoder

  override def initialState = initialSeed.fold(Random())(seed => Random.apply(seed))

  class TrivialPlayerComputation(stateIn: State, input: PlayInput) extends SimplePlayComputation[State] {
    override protected def compute: (Play, State) = {
      val boardStack = BoardStack.fromBoard(input.board)
      val choices = moveGenerator.generateMoves(boardStack, input.turnToMove)
      if(choices.count == 0) (Play.empty, stateIn)
      else {
        val (moveIndex, nextState) = {
          if(choices.count == 1) (0, stateIn)
          else stateIn.nextInt(choices.count)
        }
        moveDecoder.load(choices, moveIndex)
        val path = moveDecoder.pathToList
        val play = Play.move(path)
        (play, nextState)
      }
    }
  }

  override def play(state: State, playInput: PlayInput): PlayComputation[State] =
    new TrivialPlayerComputation(state, playInput)
}