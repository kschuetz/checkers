package checkers.computer

import checkers.core.{GameState, Opaque}

case class MoveChoiceWeights(pickBestMove: Int,
                             pickRandomMove: Int,
                             blunder: Int)


case class SearchParameters(maxDepth: Int,
                            moveChoiceWeights: MoveChoiceWeights)

trait Personality {
  def getSearchParameters(opaque: Opaque, gameState: GameState): (Opaque, SearchParameters)
}


object MoveChoiceWeights {
  val alwaysBestMove = MoveChoiceWeights(pickBestMove = 1, pickRandomMove = 0, blunder = 0)
}


class StaticPersonality(searchParameters: SearchParameters) extends Personality {
  def getSearchParameters(opaque: Opaque, gameState: GameState): (Opaque, SearchParameters) =
    (opaque, searchParameters)
}