package checkers.core

import checkers.core.tables.JumpTable

/**
  * A tree containing all legal move paths.
  * Key of the map is the from square;  value is a tree containing all moves starting or continuing from there.
  */
case class MoveTree(next: Map[Int, MoveTree], requiresJump: Boolean) {
  def isEmpty = next.isEmpty
  lazy val squares: Set[Int] = next.keySet

  def targetSquares(squareIndex: Int): Set[Int] = down(squareIndex).fold(Set.empty[Int])(_.squares)

  def walk(path: List[Int]): Option[(Int, MoveTree)] = path match {
    case Nil => None
    case x :: Nil => next.get(x).map(t => (x, t))
    case x :: xs => next.get(x).flatMap(_.walk(xs))
  }

  def down(squareIndex: Int): Option[MoveTree] = next.get(squareIndex)

  def prepend(squareIndex: Int, requiresJump: Boolean = true): MoveTree = {
    MoveTree(Map(squareIndex -> this), requiresJump = requiresJump)
  }

  override def toString: String = {
    val builder = new StringBuilder
    render(builder)
    builder.result()
  }

  private def render(output: StringBuilder): Unit = {
    if(isEmpty) output.append("end")
    else {
      output.append('(')
      if(requiresJump) output.append('J')
      var inside = false
      next.foreach { case (square, tree) =>
        if(inside) output.append(", ")
        output.append(square)
        output.append(" -> ")
        tree.render(output)
        inside = true
      }
      output.append(')')
    }
  }

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

case class MoveTreeZipper(current: MoveTree, up: Option[MoveTreeZipper]) {
  def top: MoveTree = up.fold(current)(_.top)

  def down(squareIndex: Int): Option[MoveTreeZipper] = {
    current.next.get(squareIndex).map { moveTree =>
      MoveTreeZipper(moveTree, Some(this))
    }
  }
}

object MoveTree {
  val empty = MoveTree(Map.empty, requiresJump=false)

//  def singleton(squareIndex: Int, moveTree: MoveTree): MoveTree =
//    MoveTree(Map(squareIndex -> moveTree), requiresJump = true)
}