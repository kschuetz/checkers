package checkers.computer

case class ProgressivePhase(maxCycles: Int,
                            selectionMethodWeights: MoveSelectionMethodWeights)

class ProgressivePlayer(phase1: ProgressivePhase, phase2: ProgressivePhase, mainPhase: ProgressivePhase)
  extends Personality {

  def getSearchParameters(playerState: ComputerPlayerState, playInput: PlayInput): (ComputerPlayerState, SearchParameters) = {
    val myPieceCount = playInput.board.countPieces(playInput.turnToMove)
    val phase =
      if(myPieceCount >= 11) phase1
      else if(myPieceCount >= 9) phase2
      else mainPhase

    val searchParameters = SearchParameters(None, Some(phase.maxCycles), phase.selectionMethodWeights)
    (playerState, searchParameters)
  }

}