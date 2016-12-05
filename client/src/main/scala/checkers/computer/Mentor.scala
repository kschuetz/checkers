package checkers.computer

import checkers.core.GameLogicModule


class MentorPersonality(mentorConfig: MentorConfig) extends Personality {

  def getSearchParameters(playerState: ComputerPlayerState, playInput: PlayInput): (ComputerPlayerState, SearchParameters) = {
    val myPieceCount = playInput.board.countPieces(playInput.turnToMove)
    val maxKCycles =
      if(myPieceCount >= 11) mentorConfig.Phase1MaxKCycles
      else if(myPieceCount >= 9) mentorConfig.Phase1MaxKCycles
      else if(myPieceCount >= 4) mentorConfig.MainMaxKCycles
      else mentorConfig.LateMaxKCycles

    val searchParameters = SearchParameters(None, Some(1000 * maxKCycles), MoveSelectionMethodWeights.alwaysBestMove)
    (playerState, searchParameters)
  }
}

class MentorFactory(mentorConfig: MentorConfig) extends ProgramFactory {
  private val personality = new MentorPersonality(mentorConfig)
  override def makeProgram(gameLogicModule: GameLogicModule): Program = {
    new ComputerPlayer(gameLogicModule.moveGenerator, gameLogicModule.searcher, gameLogicModule.shufflerFactory,
      personality, isMentor = true)(None)
  }
}
