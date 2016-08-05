package checkers.core

import checkers.core.tables.JumpTable

/**
  * A tree containing all legal move paths.
  * Key of the map is the from square;  value is a tree containing all moves starting or continuing from there.
  */
case class MoveTree(next: Map[Int, MoveTree], requiresJump: Boolean) {
  def isEmpty = next.isEmpty
  lazy val squares: Set[Int] = next.keySet

//  def walk(path: List[Int]): Option[MoveTree] = path match {
//    case Nil => Some(this)
//    case x :: xs => next.get(x).flatMap(_.walk(xs))
//  }

  def walk(path: List[Int]): Option[MoveTree] = path match {
    case Nil => None
    case x :: Nil => next.get(x)
    case x :: xs => next.get(x).flatMap(_.walk(xs))
  }

  // TODO: left off here 8/5/2016 - fix walk!

}

class MoveTreeFactory(jumpTable: JumpTable) {
  def fromMoveList(moveList: MoveList): MoveTree = {
    var moves = Seq.empty[List[Int]]
    moveList.foreach { decoder =>
      moves +:= decoder.pathToList
    }
    makeTree(moves)
  }

  private def makeTree(choices: Seq[List[Int]]): MoveTree = {
    if(choices.isEmpty) MoveTree.empty
    else {
      var requiresJump = false
      val pathMap = choices.foldLeft(Map.empty[Int, Seq[List[Int]]]) { case (m, moves) =>
        if(!requiresJump && jumpTable.isJump(moves)) { requiresJump = true }

        moves match {
          case Nil => m
          case square :: path =>
            val paths = m.getOrElse(square, Seq.empty)
            m + (square -> (paths :+ path))
        }
      }
      MoveTree(pathMap.mapValues(makeTree), requiresJump)
    }
  }
}

object MoveTree {
  val empty = MoveTree(Map.empty, requiresJump=false)
}