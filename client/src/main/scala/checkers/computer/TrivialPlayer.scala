package checkers.computer

import checkers.computer.TrivialPlayer._
import checkers.core._
import checkers.util.Random

object TrivialPlayer {
  case class State(value: Random) extends Opaque {
    def nextInt(n: Int): (Int, State) = {
      val (result, nextRandom) = value.nextInt(n)
      (result, State(nextRandom))
    }
  }
}

class TrivialPlayer(moveGenerator: MoveGenerator)
                   (initialSeed: Option[Long]) extends Program {

  // Can be shared between all computations
  private val moveDecoder = new MoveDecoder

  override def initialState = {
    val random = initialSeed.fold(Random())(seed => Random.apply(seed))
    State(random)
  }

  class TrivialPlayerComputation(stateIn: Opaque, input: PlayInput) extends SimplePlayComputation {
    val currentState = stateIn.asInstanceOf[State]
    override protected def compute: PlayResult = {
      val boardStack = BoardStack.fromBoard(input.board)
      val choices = moveGenerator.generateMoves(boardStack, input.turnToMove)
      if(choices.count == 0) PlayResult(Play.empty, stateIn)
      else {
        val (moveIndex, nextState) = {
          if(choices.count == 1) (0, currentState)
          else currentState.nextInt(choices.count)
        }
        moveDecoder.load(choices, moveIndex)
        val path = moveDecoder.pathToList
        val play = Play.move(path)
        PlayResult(play, nextState)
      }
    }
  }

  override def play(state: Opaque, playInput: PlayInput): PlayComputation =
    new TrivialPlayerComputation(state, playInput)

//  override def play(state: State, playInput: PlayInput): PlayComputation[State] =
//    new TrivialPlayerComputation(state, playInput)
}

class TrivialPlayerFactory extends ProgramFactory {
  def makeProgram(gameLogicModule: GameLogicModule): Program = {
    new TrivialPlayer(gameLogicModule.moveGenerator)(None)
  }
}