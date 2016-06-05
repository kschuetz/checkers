package checkers.game

case class BoardState private(squares: Vector[Occupant]) {
  def updateMany(piece: Occupant)(indices: Seq[Int]): BoardState = {
    val newState = indices.foldLeft(squares)(_.updated(_, piece))
    BoardState(newState)
  }

  def updated(squareIndex: Int, piece: Occupant): BoardState = {
    val newState = squares.updated(squareIndex, piece)
    BoardState(newState)
  }

}

case class GameState(board: BoardState,
                     turnToPlay: Color)


object BoardState {
  val empty = new BoardState(Vector.fill(32)(Empty))
}
