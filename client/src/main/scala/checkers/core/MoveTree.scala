package checkers.core

/**
  * A tree containing all legal move paths.
  * Key of the map is the from square;  value is a tree containing all moves starting or continuing from there.
  */
case class MoveTree(next: Map[Int, MoveTree]) {
  def isEmpty = next.isEmpty
}

object MoveTree {
  val empty = MoveTree(Map.empty)

  def fromMoveList(moveList: MoveList): MoveTree = {
    var moves = Seq.empty[List[Int]]
    moveList.foreach { decoder =>
      moves +:= decoder.pathToList
    }
    makeTree(moves)
  }

  private def makeTree(choices: Seq[List[Int]]): MoveTree = {
    if(choices.isEmpty) empty
    else {
      val pathMap = choices.foldLeft(Map.empty[Int, Seq[List[Int]]]) { case (m, moves) =>
        moves match {
          case Nil => m
          case square :: path =>
            val paths = m.getOrElse(square, Seq.empty)
            m + (square -> (paths :+ path))
        }
      }
      MoveTree(pathMap.mapValues(makeTree))
    }
  }
}