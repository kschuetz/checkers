package checkers.computer

import checkers.core._
import checkers.util.Random


class ComputerPlayer(moveGenerator: MoveGenerator,
                     searcher: Searcher,
                     shufflerFactory: ShufflerFactory,
                     personality: Personality)
                    (initialSeed: Option[Long]) extends Program {
  // Can be shared between all computations
  private val moveDecoder = new MoveDecoder

  override def initialState: ComputerPlayerState = {
    val random = initialSeed.fold(Random())(seed => Random.apply(seed))
    ComputerPlayerState(random)
  }

  override def play(stateIn: Opaque, playInput: PlayInput): PlayComputation = {
    val boardStack = BoardStack.fromBoard(playInput.board)
    val choices = moveGenerator.generateMoves(boardStack, playInput.turnToMove)
    if(choices.count == 0) new ImmediateResult(Play.empty, stateIn)
    else if(choices.count == 1) {
      moveDecoder.load(choices, 0)
      val path = moveDecoder.pathToList
      new ImmediateResult(Play.move(path), stateIn)
    } else {
      println("play - 1")
      println(stateIn)
      val state1 = stateIn.asInstanceOf[ComputerPlayerState]
      println("play - 2")
      val (state2, searchParameters) = personality.getSearchParameters(state1, playInput)
      println("play - 3")
      val (selectionMethod, r) = MoveSelectionMethod.getRandomMethod(searchParameters.selectionMethodWeights, state2.value)
      val state3 = ComputerPlayerState(r)

      selectionMethod match {
        case SelectRandomMove => selectRandomMove(state3, choices)
        case Blunder => search(state3, playInput, choices, searchParameters, blunder = true)
        case _ => search(state3, playInput, choices, searchParameters, blunder = false)
      }
    }
  }

  private def executeBlunder(choices: MoveList)(playResult: PlayResult): PlayResult = {
    playResult  // TODO: blunder
  }

  private def search(stateIn: ComputerPlayerState, playInput: PlayInput, choices: MoveList, searchParameters: SearchParameters, blunder: Boolean): PlayComputation = {
    //searcher
    val transformResult: PlayResult => PlayResult = if(blunder) executeBlunder(choices) else identity
    val (shuffler, state2) = shufflerFactory.createShuffler(stateIn)
    searcher.create(playInput, state2, searchParameters.depthLimit, searchParameters.cycleLimit, shuffler, transformResult)
  }

  private def selectRandomMove(stateIn: ComputerPlayerState, choices: MoveList): PlayComputation = {
    val (moveIndex, nextState) = stateIn.nextInt(choices.count)
    moveDecoder.load(choices, moveIndex)
    val path = moveDecoder.pathToList
    new ImmediateResult(Play.move(path), nextState)
  }


}

class ComputerPlayerFactory(personality: Personality) extends ProgramFactory {
  def makeProgram(gameLogicModule: GameLogicModule): Program = {
    new ComputerPlayer(gameLogicModule.moveGenerator, gameLogicModule.searcher, gameLogicModule.shufflerFactory,
      personality)(None)
  }
}