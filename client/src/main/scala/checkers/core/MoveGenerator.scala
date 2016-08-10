package checkers.core

import checkers.consts._
import checkers.core.tables.{NeighborIndex, NeighborTable}

class MoveGenerator(rulesSettings: RulesSettings,
                    moveExecutor: MoveExecutor,
                    neighborTable: NeighborTable) {

  // TODO: remove
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

  // TODO: remove
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

  // TODO: remove
  private def addMoves(moveListBuilder: MoveListBuilder, neighborIndex: NeighborIndex, moveFW: Int, moveFE: Int, moveBW: Int, moveBE: Int): Unit = {
    var i = 0
    var b = 1

    while(i < 32) {
      if((moveFE & b) != 0) {
        moveListBuilder.addMove(i.toByte, neighborIndex.forwardMoveE(i).toByte)
      }
      if((moveBE & b) != 0) {
        moveListBuilder.addMove(i.toByte, neighborIndex.backMoveE(i).toByte)
      }
      if((moveFW & b) != 0) {
        moveListBuilder.addMove(i.toByte, neighborIndex.forwardMoveW(i).toByte)
      }
      if((moveBW & b) != 0) {
        moveListBuilder.addMove(i.toByte, neighborIndex.backMoveE(i).toByte)
      }
      b = b << 1
      i += 1
    }
  }

  // TODO: remove
  private def addJumps(moveListBuilder: MoveListBuilder, neighborIndex: NeighborIndex, jumpFW: Int, jumpFE: Int, jumpBW: Int, jumpBE: Int): Unit = {
    var i = 0
    var b = 1
    while(i < 32) {
      if((jumpFE & b) != 0) {
        moveListBuilder.addMove(i.toByte, neighborIndex.forwardJumpE(i).toByte)
      }
      if((jumpBE & b) != 0) {
        moveListBuilder.addMove(i.toByte, neighborIndex.backJumpE(i).toByte)
      }
      if((jumpFW & b) != 0) {
        moveListBuilder.addMove(i.toByte, neighborIndex.forwardJumpW(i).toByte)
      }
      if((jumpBW & b) != 0) {
        moveListBuilder.addMove(i.toByte, neighborIndex.backJumpW(i).toByte)
      }
      b = b << 1
      i += 1
    }
  }

  def generateMoves(boardState: BoardStack, turnToMove: Color): MoveList = {
    val builder = new MoveListBuilder
    val movePath = new MovePathStack
    var dark = turnToMove == DARK
    val neighborIndex = neighborTable.forColor(turnToMove)

    import masks._

    def go(limitToPieces: Int, jumpsOnly: Boolean): Boolean = {
      val myPieces = if(dark) boardState.darkPieces else boardState.lightPieces
      val myPiecesOfInterest = myPieces & limitToPieces
      val opponentPieces = if(dark) boardState.lightPieces else boardState.darkPieces
      val kings = boardState.kings
      val notOccupied = ~(myPieces | opponentPieces)
      val myKings = myPiecesOfInterest & kings

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
        oppBE = shiftSE(opponentPieces)

      } else {

        noBW = shiftNW(notOccupied)
        noBW2 = shiftNW(noBW)
        noBE = shiftNE(notOccupied)
        noBE2 = shiftNE(noBE)

        oppBW = shiftNW(opponentPieces)
        oppBE = shiftNE(opponentPieces)
      }

      val jumpFE = myPiecesOfInterest & oppBW & noBW2
      val jumpFW = myPiecesOfInterest & oppBE & noBE2

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
        // add jumps

        def followJump(from: Byte, to: Byte): Unit = {
          val pathMarker = movePath.mark
          movePath.push(from)
          boardState.push()
          val endOfMove = {
            val crowned = moveExecutor.fastExecute(boardState, from, to)
            if (crowned) {
              // when a piece is crowned, it is immediately the end of the move
              true
            } else {
              // on the next check, only check for jumps occurring on the 'to' square from this jump
              val more = go(limitToPieces = 1 << to, jumpsOnly = true)
              !more
            }
          }

          if(endOfMove) {
            movePath.push(to)
            builder.addPath(movePath)
          }
          movePath.reset(pathMarker)
          boardState.pop()
        }

        val forwardJumpE = neighborIndex.forwardJumpE
        val forwardJumpW = neighborIndex.forwardJumpW
        val backJumpW = neighborIndex.backJumpW
        val backJumpE = neighborIndex.backJumpE

        var i = 0
        var b = 1
        while(i < 32) {
          if((jumpFE & b) != 0) {
            followJump(i.toByte, forwardJumpE(i).toByte)
          }
          if((jumpBE & b) != 0) {
            followJump(i.toByte, backJumpE(i).toByte)
          }
          if((jumpFW & b) != 0) {
            followJump(i.toByte, forwardJumpW(i).toByte)
          }
          if((jumpBW & b) != 0) {
            followJump(i.toByte, backJumpW(i).toByte)
          }
          b = b << 1
          i += 1
        }

      } else if(!jumpsOnly) {

        moveFE = myPiecesOfInterest & noBW
        moveFW = myPiecesOfInterest & noBE

        if (myKings != 0) {
          moveBE = myKings & noFW
          moveBW = myKings & noFE
        }

        val hasMoves = (moveFE | moveFW | moveBW | moveBW) != 0
        if (hasMoves) {
          // add moves

          val forwardMoveE = neighborIndex.forwardMoveE
          val forwardMoveW = neighborIndex.forwardMoveW
          val backMoveW = neighborIndex.backMoveW
          val backMoveE = neighborIndex.backMoveE

          var i = 0
          var b = 1

          while(i < 32) {
            if((moveFE & b) != 0) {
              builder.addMove(i.toByte, forwardMoveE(i).toByte)
            }
            if((moveBE & b) != 0) {
              builder.addMove(i.toByte, backMoveE(i).toByte)
            }
            if((moveFW & b) != 0) {
              builder.addMove(i.toByte, forwardMoveW(i).toByte)
            }
            if((moveBW & b) != 0) {
              builder.addMove(i.toByte, backMoveW(i).toByte)
            }
            b = b << 1
            i += 1
          }
        }
      }

      hasJumps
    }

    go(limitToPieces = -1, jumpsOnly = false)
    builder.result
  }

}

