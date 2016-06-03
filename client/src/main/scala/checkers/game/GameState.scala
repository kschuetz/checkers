package checkers.game

case class GameState(squares: Vector[Occupant])


object GameState {
  val emptyBoard: Vector[Occupant] = Vector.fill(32)(Empty)
}