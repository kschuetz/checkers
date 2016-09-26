package checkers.computer


case class SearchParameters(maxDepth: Option[Int],
                            maxCycles: Option[Int],
                            selectionMethodWeights: MoveSelectionMethodWeights)

trait Personality {
  def getSearchParameters(playerState: ComputerPlayerState, playInput: PlayInput): (ComputerPlayerState, SearchParameters)
}


class StaticPersonality(searchParameters: SearchParameters) extends Personality {
  def getSearchParameters(playerState: ComputerPlayerState, playInput: PlayInput): (ComputerPlayerState, SearchParameters) =
    (playerState, searchParameters)
}


