package checkers.computer


case class SearchParameters(depthLimit: Option[Int],
                            cycleLimit: Option[Int],
                            selectionMethodWeights: MoveSelectionMethodWeights)

trait Personality {
  def getSearchParameters(playerState: ComputerPlayerState, playInput: PlayInput): (ComputerPlayerState, SearchParameters)
}


class StaticPersonality(searchParameters: SearchParameters) extends Personality {
  def getSearchParameters(playerState: ComputerPlayerState, playInput: PlayInput): (ComputerPlayerState, SearchParameters) =
    (playerState, searchParameters)
}


