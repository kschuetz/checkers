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


  private def addMoves(moveListBuilder: MoveListBuilder, color: Color, moveNW: Int, moveNE: Int, moveSW: Int, moveSE: Int): Unit = {
    var i = 0
    var b = 1
    while(i < 32) {
      if((moveNE & b) != 0) {
        moveListBuilder.addMove(i.toByte, NeighborIndex.moveNE(i).toByte)
      }
      if((moveSE & b) != 0) {
        moveListBuilder.addMove(i.toByte, NeighborIndex.moveSE(i).toByte)
      }
      if((moveNW & b) != 0) {
        moveListBuilder.addMove(i.toByte, NeighborIndex.moveNW(i).toByte)
      }
      if((moveSW & b) != 0) {
        moveListBuilder.addMove(i.toByte, NeighborIndex.moveSW(i).toByte)
      }
      b = b << 1
      i += 1
    }
  }

  private def addJumps(moveListBuilder: MoveListBuilder, color: Color, jumpNW: Int, jumpNE: Int, jumpSW: Int, jumpSE: Int): Unit = {
    var i = 0
    var b = 1
    while(i < 32) {
      if((jumpNE & b) != 0) {
        moveListBuilder.addMove(i.toByte, NeighborIndex.jumpNE(i).toByte)
      }
      if((jumpSE & b) != 0) {
        moveListBuilder.addMove(i.toByte, NeighborIndex.jumpSE(i).toByte)
      }
      if((jumpNW & b) != 0) {
        moveListBuilder.addMove(i.toByte, NeighborIndex.jumpNW(i).toByte)
      }
      if((jumpSW & b) != 0) {
        moveListBuilder.addMove(i.toByte, NeighborIndex.jumpSW(i).toByte)
      }
      b = b << 1
      i += 1
    }
  }

  def generateMovesLight(boardState: BoardStack): MoveList = {
    val (myPieces, opponentPieces) = (boardState.lightPieces, boardState.darkPieces)
    val kings = boardState.kings
    val notOccupied = ~(myPieces | opponentPieces)
    val myKings = myPieces & kings

    val builder = new MoveListBuilder

    import masks._

    var moveSE = 0
    var moveSW = 0
    var moveNE = 0
    var moveNW = 0

    val noNW = shiftNW(notOccupied)
    val noNW2 = shiftNW(noNW)
    val noNE = shiftNE(notOccupied)
    val noNE2 = shiftNE(noNE)
    var noSW = 0
    var noSE = 0

    val oppNW = shiftNW(opponentPieces)
    val oppNE = shiftNE(opponentPieces)

    val jumpSE = myPieces & oppNW & noNW2
    val jumpSW = myPieces & oppNE & noNE2

    var jumpNE = 0
    var jumpNW = 0
    if(myKings != 0) {
      noSW = shiftSW(notOccupied)
      noSE = shiftSE(notOccupied)

      val noSW2 = shiftSW(noSW)
      val noSE2 = shiftSE(noSE)

      val oppSW = shiftSW(opponentPieces)
      val oppSE = shiftSE(opponentPieces)

      jumpNE = myKings & oppSW & noSW2
      jumpNW = myKings & oppSE & noSE2
    }

    val hasJumps = (jumpSE | jumpSW | jumpNE | jumpNW) != 0
    if(hasJumps) {
      addJumps(builder, LIGHT, jumpNW, jumpNE, jumpSW, jumpSE)
    } else {

      moveSE = myPieces & noNW
      moveSW = myPieces & noNE

      if(myKings != 0) {
        moveNE = myKings & noSW
        moveNW = myKings & noSE
      }

      val hasMoves = (moveSE | moveSW | moveNW | moveSE) != 0
      if(hasMoves) {
        addMoves(builder, LIGHT, moveNW, moveNE, moveSW, moveSE)
      }
    }

    builder.result
  }


  def generateMovesDark(boardState: BoardStack): MoveList = {
    val (myPieces, opponentPieces) = (boardState.darkPieces, boardState.lightPieces)
    val kings = boardState.kings
    val notOccupied = ~(myPieces | opponentPieces)
    val myKings = myPieces & kings

    val builder = new MoveListBuilder

    import masks._

    var moveNE = 0
    var moveNW = 0
    var moveSE = 0
    var moveSW = 0

    val noSW = shiftSW(notOccupied)
    val noSW2 = shiftSW(noSW)
    val noSE = shiftSE(notOccupied)
    val noSE2 = shiftSE(noSE)
    var noNW = 0
    var noNE = 0

    val oppSW = shiftSW(opponentPieces)
    val oppSE = shiftSE(opponentPieces)

    val jumpNE = myPieces & oppSW & noSW2
    val jumpNW = myPieces & oppSE & noSE2

    var jumpSE = 0
    var jumpSW = 0
    if(myKings != 0) {
      noNW = shiftNW(notOccupied)
      noNE = shiftNE(notOccupied)

      val noNW2 = shiftNW(noNW)
      val noNE2 = shiftNE(noNE)

      val oppNW = shiftNW(opponentPieces)
      val oppNE = shiftNE(opponentPieces)

      jumpSE = myKings & oppNW & noNW2
      jumpSW = myKings & oppNE & noNE2
    }

    val hasJumps = (jumpNE | jumpNW | jumpSE | jumpSW) != 0
    if(hasJumps) {
      addJumps(builder, DARK, jumpNW, jumpNE, jumpSW, jumpSE)
    } else {

      moveNE = myPieces & noSW
      moveNW = myPieces & noSE

      if(myKings != 0) {
        moveSE = myKings & noNW
        moveSW = myKings & noNE
      }

      val hasMoves = (moveNE | moveNW | moveSW | moveSW) != 0
      if(hasMoves) {
        addMoves(builder, DARK, moveNW, moveNE, moveSW, moveSE)
      }
    }

    builder.result
  }


  def generateMoves(boardState: BoardStack, turnToMove: Color): MoveList = {
    val builder = new MoveListBuilder
    var dark = turnToMove == DARK

    import masks._

    def go(): Unit = {
      val myPieces = if(dark) boardState.darkPieces else boardState.lightPieces
      val opponentPieces = if(dark) boardState.lightPieces else boardState.darkPieces
      val kings = boardState.kings
      val notOccupied = ~(myPieces | opponentPieces)
      val myKings = myPieces & kings

      var moveFE = 0
      var moveFW = 0
      var moveBE = 0
      var moveBW = 0

      var noBW = 0
      var noBW2 = 0
      var noBE = 0
      var noBE2 = 0
      var noFW = 0
      var noFW2 = 0
      var noFE = 0
      var noFE2 = 0

      var oppFW = 0
      var oppBW = 0
      var oppFE = 0
      var oppBE = 0

      if(dark) {
        noBW = shiftSW(notOccupied)
        noBW2 = shiftSW(noBW)
        noBE = shiftSE(notOccupied)
        noBE2 = shiftSE(noBE)

        oppBW = shiftSW(opponentPieces)

      } else {

        noBW = shiftNW(notOccupied)
        noBW2 = shiftNW(noBW)
        noBE = shiftNE(notOccupied)
        noBE2 = shiftNE(noBE)

        oppBW = shiftNW(opponentPieces)
        oppBE = shiftNE(opponentPieces)
      }

      val jumpFE = myPieces & oppBW & noBW2
      val jumpFW = myPieces & oppBE & noBE2

      var jumpBE = 0
      var jumpBW = 0

      if (myKings != 0) {


        if(dark) {
          noFW = shiftNW(notOccupied)
          noFE = shiftNE(notOccupied)

          noFW2 = shiftNW(noFW)
          noFE2 = shiftNE(noFE)

          oppFW = shiftNW(opponentPieces)
          oppFE = shiftNE(opponentPieces)
        } else {
          noFW = shiftSW(notOccupied)
          noFE = shiftSE(notOccupied)

          noFW2 = shiftSW(noFW)
          noFE2 = shiftSE(noFE)

          oppFW = shiftSW(opponentPieces)
          oppFE = shiftSE(opponentPieces)
        }

        jumpBE = myKings & oppFW & noFW2
        jumpBW = myKings & oppFE & noFE2
      }

      val hasJumps = (jumpFW | jumpFE | jumpBW | jumpBE) != 0
      if (hasJumps) {
        if(dark) {
          addJumps(builder, DARK, jumpFW, jumpFE, jumpBW, jumpBE)
        } else {
          addJumps(builder, LIGHT, jumpBW, jumpBE, jumpFW, jumpFE)
        }
      } else {

        moveFE = myPieces & noBW
        moveFW = myPieces & noBE

        if (myKings != 0) {
          moveBE = myKings & noFW
          moveBW = myKings & noFE
        }

        val hasMoves = (moveFE | moveFW | moveBW | moveBW) != 0
        if (hasMoves) {
          if(dark) {
            addMoves(builder, DARK, moveFW, moveFE, moveBW, moveBE)
          } else {
            addMoves(builder, DARK, moveBW, moveBE, moveFW, moveFE)
          }
        }
      }
    }

    go()
    builder.result
  }


  def generateMoves2(boardState: BoardStack, turnToMove: Color): MoveList =
    if(turnToMove == DARK) generateMovesDark(boardState)
    else generateMovesLight(boardState)



}

