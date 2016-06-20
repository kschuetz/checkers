package checkers.core

import checkers.consts._

class MoveGenerator(rulesSettings: RulesSettings) {


  def getJumpersDark(boardState: BoardStateRead): Int = {
    val (myPieces, opponentPieces) = (boardState.darkPieces, boardState.lightPieces)
    val kings = boardState.kings
    val notOccupied = ~(myPieces | opponentPieces)
    val myKings = myPieces & kings

    var result = 0
    var temp = (notOccupied >> 4) & opponentPieces
    if(temp != 0) {
      result |= (((temp & masks.r3) >> 3) | ((temp & masks.r5) >> 5)) & myPieces
    }
    temp = (((notOccupied & masks.r3) >> 3) | ((notOccupied & masks.r5) >> 5)) & opponentPieces
    result |= (temp >> 4) & myPieces
    if(myKings != 0) {
      temp = (notOccupied << 4) & opponentPieces
      if(temp != 0) {
        result |= (((temp & masks.l3) << 3) | ((temp & masks.l5) << 5)) & myKings
      }
      temp = (((notOccupied & masks.l3) << 3) | ((notOccupied & masks.l5) << 5)) & opponentPieces
      if(temp != 0) result |= (temp << 4) & myKings
    }
    result
  }

  def getJumpersLight(boardState: BoardStateRead): Int = {
    val (myPieces, opponentPieces) = (boardState.lightPieces, boardState.darkPieces)
    val kings = boardState.kings
    val notOccupied = ~(myPieces | opponentPieces)
    val myKings = myPieces & kings

    var result = 0
    var temp = (notOccupied << 4) & opponentPieces
    if(temp != 0) {
      result |= (((temp & masks.l3) << 3) | ((temp & masks.l5) << 5)) & myPieces
    }
    temp = (((notOccupied & masks.l3) << 3) | ((notOccupied & masks.l5) << 5)) & opponentPieces
    result |= (temp << 4) & myPieces
    if(myKings != 0) {
      temp = (notOccupied << 4) & opponentPieces
      if(temp != 0) {
        result |= (((temp & masks.r3) >> 3) | ((temp & masks.r5) >> 5)) & myKings
      }
      temp = (((notOccupied & masks.r3) >> 3) | ((notOccupied & masks.r5) >> 5)) & opponentPieces
      if(temp != 0) result |= (temp >> 4) & myKings
    }
    result
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

