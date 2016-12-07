package checkers.core

import checkers.core.tables.JumpTable

class Notation(jumpTable: JumpTable) {

  def notationForPlay(play: Play): Option[String] = play match {
    case Play.Move(path, _) =>
      val separator = if(jumpTable.isJump(path)) "x" else "-"
      val squares = path.map(notationForSquare)
      Some(squares.mkString(separator))
    case _ => None
  }

  // TODO: convert to official notation
  private def notationForSquare(square: Int): String = square.toString
}