package checkers.computer

import checkers.util.Random

sealed trait MoveSelectionMethod
case object SelectBestMove extends MoveSelectionMethod
case object SelectRandomMove extends MoveSelectionMethod
case object Blunder extends MoveSelectionMethod

case class MoveSelectionMethodWeights(selectBestMove: Int,
                                      selectRandomMove: Int,
                                      blunder: Int) {
  def totalWeight = selectBestMove + selectRandomMove + blunder
}

object MoveSelectionMethodWeights {
  val alwaysBestMove = MoveSelectionMethodWeights(selectBestMove = 1, selectRandomMove = 0, blunder = 0)
}

object MoveSelectionMethod {
  def getRandomMethod(weights: MoveSelectionMethodWeights, random: Random): (MoveSelectionMethod, Random) = {
    val total = weights.totalWeight
    if(total <= 0 || weights.selectBestMove == total) (SelectBestMove, random)
    else if (weights.selectRandomMove == total) (SelectRandomMove, random)
    else if (weights.blunder == total) (Blunder, random)
    else {
      val randomMoveThreshold = weights.selectRandomMove
      val blunderThreshold = randomMoveThreshold + weights.blunder

      val (n, randomOut) = random.nextInt(total)

      if(n < randomMoveThreshold) (SelectRandomMove, randomOut)
      else if(n < blunderThreshold) (Blunder, randomOut)
      else (SelectBestMove, randomOut)
    }
  }

}