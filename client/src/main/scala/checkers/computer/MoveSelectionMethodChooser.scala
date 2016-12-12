package checkers.computer

import checkers.util.Random

trait MoveSelectionMethodChooser {
  def chooseMethod(weights: MoveSelectionMethodWeights, random: Random): (MoveSelectionMethod, Random)
}


object DefaultMoveSelectionMethodChooser extends MoveSelectionMethodChooser {
  def chooseMethod(weights: MoveSelectionMethodWeights, random: Random): (MoveSelectionMethod, Random) = {
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

// used in testing and balancing to disable blunders and random move selection
object AlwaysSelectBestMove extends MoveSelectionMethodChooser {
  override def chooseMethod(weights: MoveSelectionMethodWeights, random: Random): (MoveSelectionMethod, Random) =
    (SelectBestMove, random)
}