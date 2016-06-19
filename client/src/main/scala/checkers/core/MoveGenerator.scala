package checkers.core

import checkers.consts._

class MoveGenerator(rulesSettings: RulesSettings) {


  def getJumpersDark(boardState: BoardStateRead): Int = {
    val (myPieces, opponentPieces) = (boardState.darkPieces, boardState.lightPieces)
    val kings = boardState.kings
    val occupied = myPieces | opponentPieces
    val myKings = myPieces & kings

    // TODO: left off 6/19
//    // get jumpers
//    var jumpers = 0
//    var temp = (occupied << 4) & opponentPieces
//    if(temp != 0) {
//      jumpers |= (((temp & f3) << 3) | ((temp & f5) << 5)) & myPieces
//    }
//    jumpers |= (temp << 4) & myPieces
//    if(myKings != 0) {
//      temp = (occupied >> 4) & opponentPieces
//      if(temp != 0) {
//        jumpers |= (((temp & b3) << 3) | ((temp & b5) << 5)) & myPieces
//      }
    0
  }

  def generateMoves(boardState: BoardStack, turnToMove: Color): MoveList = {
    val builder = new MoveListBuilder

    val (myPieces, opponentPieces, f3, f5, b3, b5) = if(turnToMove == DARK) {
      (boardState.darkPieces, boardState.lightPieces, masks.r3, masks.r5, masks.l3, masks.l5)
    } else {
      (boardState.lightPieces, boardState.darkPieces, masks.l3, masks.l5, masks.r3, masks.r5)
    }
    val kings = boardState.kings

    builder.result
  }
}

