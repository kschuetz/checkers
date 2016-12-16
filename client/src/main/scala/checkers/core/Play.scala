package checkers.core

sealed trait Play {
  def getFinalSegment: Option[(Int, Int)]
}

object Play {
  case object NoPlay extends Play {
    def getFinalSegment: Option[(Int, Int)] = None
  }

  case class Move(path: List[Int], proposeDraw: Boolean = false) extends Play {
    def getFinalSegment: Option[(Int, Int)] = {
      def go(items: List[Int]): Option[(Int, Int)] = items match {
        case Nil => None
        case _ :: Nil => None
        case x :: y :: Nil => Some(x, y)
        case x :: xs => go(xs)
      }
      go(path)
    }
  }

  val empty = NoPlay

  def move(path: List[Int]): Move = Play.Move(path)

  def move(fromSquare: Int, toSquare: Int): Move = Play.Move(fromSquare :: toSquare :: Nil)

  def swapSides(play: Play): Play = play match {
    case NoPlay => NoPlay
    case move: Move => move.copy(path = MoveList.invertPath(move.path))
  }
}


