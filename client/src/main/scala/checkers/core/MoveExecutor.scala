package checkers.core

import checkers.consts._

class MoveExecutor(rulesSettings: RulesSettings) {

  /**
    * Updates the board in place.  Does not return metadata, other than a flag indicating a crowning event.
    * @return if true, move ended in a piece being crowned
    */
  def fastExecute(boardState: MutableBoardState, move: Move): Boolean = {
    var crowned = false

    def runSimple(move: SimpleMove): Unit = {
      val piece = boardState.getOccupant(move.from)
      if(move.over >= 0) boardState.setOccupant(move.over, EMPTY)
      boardState.setOccupant(move.from, EMPTY)
      if(Board.isCrowningMove(piece, move.to)) {
        crowned = true
        boardState.setOccupant(move.to, Occupant.crowned(piece))
      }
    }

    // runs reverse of path
    def runCompound(path: List[SimpleMove]): Unit = path match {
      case Nil => ()
      case x :: xs =>
        runCompound(xs)
        runSimple(x)
    }

    move match {
      case move: SimpleMove => runSimple(move)
      case CompoundMove(path) => runCompound(path)
    }
    crowned
  }


}