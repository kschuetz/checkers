package checkers.core

sealed trait Move
case class SimpleMove(from: Int, to: Int) extends Move
case class Jump(from: Int, to: Int) extends Move



class MoveGenerator(rulesSettings: RulesSettings) {

  type MoveList = Seq[Move]

  def generateMoves(boardState: BoardStateRead, turnToMove: Color): MoveList = {
    val neighborIndex = turnToMove match {
      case Dark => DarkNeighborIndex
      case Light => LightNeighborIndex
    }
    ???
  }



}