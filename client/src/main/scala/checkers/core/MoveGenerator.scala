package checkers.core

import checkers.consts._
import checkers.core.tables.NeighborTable
import checkers.masks._

trait MoveGenerator {
  def generateMoves(boardState: BoardStack, turnToMove: Side): MoveList

  def mustJump(boardState: BoardStateRead, turnToMove: Side): Boolean
}


class DefaultMoveGenerator(rulesSettings: RulesSettings,
                           moveExecutor: MoveExecutor,
                           neighborTable: NeighborTable) extends MoveGenerator {

  def generateMoves(boardState: BoardStack, turnToMove: Side): MoveList = {
    val builder = new MoveListBuilder
    val movePath = new MovePathStack
    val dark = turnToMove == DARK
    val neighborIndex = neighborTable.forSide(turnToMove)

    import checkers.masks._

    def go(limitToPieces: Int, jumpsOnly: Boolean): Boolean = {
      val myPieces = if (dark) boardState.darkPieces else boardState.lightPieces
      val myPiecesOfInterest = myPieces & limitToPieces
      val opponentPieces = if (dark) boardState.lightPieces else boardState.darkPieces
      val kings = boardState.kings
      val notOccupied = ~(myPieces | opponentPieces)
      val myKings = myPiecesOfInterest & kings

      var moveFrontR = 0
      var moveFrontL = 0
      var moveBackR = 0
      var moveBackL = 0

      var noFrontR = 0
      var noFrontR2 = 0
      var noFrontL = 0
      var noFrontL2 = 0
      var noBackR = 0
      var noBackR2 = 0
      var noBackL = 0
      var noBackL2 = 0

      var oppBackR = 0
      var oppFrontR = 0
      var oppBackL = 0
      var oppFrontL = 0

      if (dark) {
        noFrontR = SHIFTSW(notOccupied)
        noFrontR2 = SHIFTSW(noFrontR)
        noFrontL = SHIFTSE(notOccupied)
        noFrontL2 = SHIFTSE(noFrontL)

        oppFrontR = SHIFTSW(opponentPieces)
        oppFrontL = SHIFTSE(opponentPieces)

      } else {
        noFrontR = SHIFTNW(notOccupied)
        noFrontR2 = SHIFTNW(noFrontR)
        noFrontL = SHIFTNE(notOccupied)
        noFrontL2 = SHIFTNE(noFrontL)

        oppFrontR = SHIFTNW(opponentPieces)
        oppFrontL = SHIFTNE(opponentPieces)
      }

      val jumpFrontR = myPiecesOfInterest & oppFrontR & noFrontR2
      val jumpFrontL = myPiecesOfInterest & oppFrontL & noFrontL2

      var jumpBackR = 0
      var jumpBackL = 0

      if (myKings != 0) {

        if (dark) {
          noBackR = SHIFTNW(notOccupied)
          noBackL = SHIFTNE(notOccupied)

          noBackR2 = SHIFTNW(noBackR)
          noBackL2 = SHIFTNE(noBackL)

          oppBackR = SHIFTNW(opponentPieces)
          oppBackL = SHIFTNE(opponentPieces)
        } else {
          noBackR = SHIFTSW(notOccupied)
          noBackL = SHIFTSE(notOccupied)

          noBackR2 = SHIFTSW(noBackR)
          noBackL2 = SHIFTSE(noBackL)

          oppBackR = SHIFTSW(opponentPieces)
          oppBackL = SHIFTSE(opponentPieces)
        }

        jumpBackR = myKings & oppBackR & noBackR2
        jumpBackL = myKings & oppBackL & noBackL2
      }

      val hasJumps = (jumpFrontL | jumpFrontR | jumpBackL | jumpBackR) != 0
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

          if (endOfMove) {
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
        while (i < 32) {
          if ((jumpFrontR & b) != 0) {
            followJump(i.toByte, forwardJumpE(i).toByte)
          }
          if ((jumpBackR & b) != 0) {
            followJump(i.toByte, backJumpE(i).toByte)
          }
          if ((jumpFrontL & b) != 0) {
            followJump(i.toByte, forwardJumpW(i).toByte)
          }
          if ((jumpBackL & b) != 0) {
            followJump(i.toByte, backJumpW(i).toByte)
          }
          b = b << 1
          i += 1
        }

      } else if (!jumpsOnly) {

        moveFrontR = myPiecesOfInterest & noFrontR
        moveFrontL = myPiecesOfInterest & noFrontL

        if (myKings != 0) {
          moveBackR = myKings & noBackR
          moveBackL = myKings & noBackL
        }

        val hasMoves = (moveFrontR | moveFrontL | moveBackL | moveBackR) != 0
        if (hasMoves) {
          // add moves

          val forwardMoveE = neighborIndex.forwardMoveE
          val forwardMoveW = neighborIndex.forwardMoveW
          val backMoveW = neighborIndex.backMoveW
          val backMoveE = neighborIndex.backMoveE

          var i = 0
          var b = 1

          while (i < 32) {
            if ((moveFrontR & b) != 0) {
              builder.addMove(i.toByte, forwardMoveE(i).toByte)
            }
            if ((moveBackR & b) != 0) {
              builder.addMove(i.toByte, backMoveE(i).toByte)
            }
            if ((moveFrontL & b) != 0) {
              builder.addMove(i.toByte, forwardMoveW(i).toByte)
            }
            if ((moveBackL & b) != 0) {
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

  def mustJump(boardState: BoardStateRead, turnToMove: Side): Boolean = {
    val dark = turnToMove == DARK
    val myPieces = if (dark) boardState.darkPieces else boardState.lightPieces
    val opponentPieces = if (dark) boardState.lightPieces else boardState.darkPieces
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

    if (dark) {
      noBW = SHIFTSW(notOccupied)
      noBW2 = SHIFTSW(noBW)
      noBE = SHIFTSE(notOccupied)
      noBE2 = SHIFTSE(noBE)

      oppBW = SHIFTSW(opponentPieces)
      oppBE = SHIFTSE(opponentPieces)

    } else {

      noBW = SHIFTNW(notOccupied)
      noBW2 = SHIFTNW(noBW)
      noBE = SHIFTNE(notOccupied)
      noBE2 = SHIFTNE(noBE)

      oppBW = SHIFTNW(opponentPieces)
      oppBE = SHIFTNE(opponentPieces)
    }

    val jumpFE = myPieces & oppBW & noBW2
    val jumpFW = myPieces & oppBE & noBE2

    var jumpBE = 0
    var jumpBW = 0

    if (myKings != 0) {

      if (dark) {
        noFW = SHIFTNW(notOccupied)
        noFE = SHIFTNE(notOccupied)

        noFW2 = SHIFTNW(noFW)
        noFE2 = SHIFTNE(noFE)

        oppFW = SHIFTNW(opponentPieces)
        oppFE = SHIFTNE(opponentPieces)
      } else {
        noFW = SHIFTSW(notOccupied)
        noFE = SHIFTSE(notOccupied)

        noFW2 = SHIFTSW(noFW)
        noFE2 = SHIFTSE(noFE)

        oppFW = SHIFTSW(opponentPieces)
        oppFE = SHIFTSE(opponentPieces)
      }

      jumpBE = myKings & oppFW & noFW2
      jumpBW = myKings & oppFE & noFE2
    }

    (jumpFW | jumpFE | jumpBW | jumpBE) != 0
  }

}

