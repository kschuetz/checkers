package checkers.core

import checkers.core.tables.JumpTable

class Notation(jumpTable: JumpTable) {

  val numberForSquare: Vector[Int] = (0 to 31).toVector.map { idx =>
    4 * (idx / 4) + (4 - idx % 4)
  }

  private val notationForSquare: Vector[String] = numberForSquare.map(_.toString)

  def notationForPlay(play: Play): Option[String] = play match {
    case Play.Move(path, _) =>
      val separator = if(jumpTable.isJump(path)) "x" else "-"
      val squares = path.map(notationForSquare)
      Some(squares.mkString(separator))
    case _ => None
  }



}