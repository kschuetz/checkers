package checkers.computer

sealed trait MoveSelectionMethod
case object SelectBestMove extends MoveSelectionMethod
case object SelectRandomMove extends MoveSelectionMethod
case object Blunder extends MoveSelectionMethod

case class MoveSelectionMethodWeights(selectBestMove: Int,
                                      selectRandomMove: Int,
                                      blunder: Int) {
  def totalWeight: Int = selectBestMove + selectRandomMove + blunder

  def debugInfoString: String = {
    val total = 1.0 * totalWeight
    if(total == 0) "INVALID"
    else f"best: ${selectBestMove / total}%.4f, random: ${selectRandomMove / total}%.4f, blunder: ${blunder / total}%.4f"
  }
}

object MoveSelectionMethodWeights {
  val alwaysBestMove: MoveSelectionMethodWeights = MoveSelectionMethodWeights(selectBestMove = 1, selectRandomMove = 0, blunder = 0)
}
