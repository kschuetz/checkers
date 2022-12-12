package checkers.util

import checkers.core.{MoveDecoder, MoveList}

object MoveListPrinter {

  private def movePathToString(decoder: MoveDecoder): String =
    decoder.pathToList.mkString("(", " -> ", ")")

  def moveListToString(moveList: MoveList): String = {
    val decoder = new MoveDecoder
    var moves = List.empty[String]
    val len = moveList.count
    var i = 0
    while(i < len) {
      decoder.load(moveList, i)
      val path = movePathToString(decoder)
      moves = path :: moves
      i += 1
    }
    moves.reverse.mkString("[", ", ", "]")
  }

}