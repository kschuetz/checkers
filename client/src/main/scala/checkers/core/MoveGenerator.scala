package checkers.core

import scala.scalajs.js

import checkers.consts._


class MoveGenerator(rulesSettings: RulesSettings,
                    moveExecutor: MoveExecutor) {

  private val jumpsCompulsory = true

  def generateMoves(boardState: BoardStack, turnToMove: Color): MoveList = {
    val opponent = if(turnToMove == LIGHT) DARK else LIGHT
    val neighborIndex = NeighborIndex.forColor(turnToMove)
    import neighborIndex._

    var processNormalMoves = true
    val builder = new MoveListBuilder

    def tryJump(path: List[SimpleMove], piece: Occupant, from: Int, over: Int, target: Int): Boolean = {
      if (over < 0 || target < 0) return false
      if (!boardState.isSquareEmpty(target)) return false
      if (!boardState.squareHasColor(opponent, over)) return false
      boardState.push()
      val move = SimpleMoveIndex(from, target)
      val newPath = move :: path
      val crowned = moveExecutor.fastExecute(boardState, move)
      if (!crowned) {
        val haveMore = !tryJumps(newPath, piece, target)
        if (!(haveMore && jumpsCompulsory)) {
          builder.addPath(newPath)
        }
      }
      if (jumpsCompulsory) {
        processNormalMoves = false
      }
      boardState.pop()
      true
    }

    def tryJumps(path: List[SimpleMove], piece: Occupant, from: Int): Boolean = {
      var result = false
      result = tryJump(path, piece, from, forwardMoveW(from), forwardJumpW(from))
      result ||= tryJump(path, piece, from, forwardMoveE(from), forwardJumpE(from))
      if (ISKING(piece)) {
        result ||= tryJump(path, piece, from, backMoveW(from), backJumpW(from))
        result ||= tryJump(path, piece, from, backMoveE(from), backJumpE(from))
      }
      result
    }

    def tryMove(from: Int, moveNeighbor: js.Array[Int]): Unit = {
      val target = moveNeighbor(from)
      if (target < 0) return
      if (!boardState.isSquareEmpty(target)) return
      builder.addSimpleMove(SimpleMoveIndex(from, target))
    }

    boardState.foreach(turnToMove){ case (square, piece) =>
      tryJumps(Nil, piece, square)
    }

    if(processNormalMoves) {
      boardState.foreach(turnToMove){ case (square, piece) =>
        tryMove(square, forwardMoveW)
        tryMove(square, forwardMoveE)
        if (ISKING(piece)) {
          tryMove(square, backMoveW)
          tryMove(square, backMoveE)
        }
      }
    }



    builder.result
  }
}