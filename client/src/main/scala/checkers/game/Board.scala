package checkers.game

object Board {

  // squareIndex: 0..31
  def squareIndexToRowCol(squareIndex: Int): (Int, Int) = {
    val row = squareIndex / 4
    val col = if (row % 2 == 0) 1 + 2 * (squareIndex % 4)
              else 2 * (squareIndex % 4)

    (row, col)
  }

  val lightStartingSquares = 0 to 11
  val darkStartingSquares = 20 to 31

}