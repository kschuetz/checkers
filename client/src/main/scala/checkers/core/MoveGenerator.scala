package checkers.core

import checkers.consts._

class MoveGenerator(rulesSettings: RulesSettings) {

  def generateMoves(boardState: BoardStack, turnToMove: Color): MoveList = {
    val builder = new MoveListBuilder

    val (myPieces, opponentPieces) = if(turnToMove == DARK) {
      (boardState.darkPieces, boardState.lightPieces)
    } else {
      (boardState.lightPieces, boardState.darkPieces)
    }
    val kings = boardState.kings

    val occupied = myPieces | opponentPieces
    val myKings = myPieces & kings

    var jumpers = 0
    var temp = (occupied << 4) & opponentPieces



    builder.result
  }
}


object MoveGenerator {

  object masks {



  }




}