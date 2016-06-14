package checkers.core

import scala.scalajs.js


class MoveGenerator(rulesSettings: RulesSettings,
                    moveExecutor: MoveExecutor) {

  private val jumpsCompulsory = true

  def generateMoves(boardState: BoardStack, turnToMove: Color): MoveList = {
    val opponent = turnToMove.opposite
    val neighborIndex = NeighborIndex.forColor(turnToMove)
    import neighborIndex._

    var processNormalMoves = true
    val builder = new MoveListBuilder

    def tryJump(moveStack: List[SimpleMove], piece: Piece, from: Int, over: Int, target: Int): Boolean = {
      if (over < 0 || target < 0) return false
      if (!boardState.isSquareEmpty(target)) return false
      if (!boardState.squareHasColor(opponent, over)) return false
      boardState.push()
      val move = SimpleMoveIndex(from, target)
      val newStack = move :: moveStack
      val crowned = moveExecutor.fastExecute(boardState, move)
      if (!crowned) {
        val haveMore = !tryJumps(newStack, piece, target)
        if (!(haveMore && jumpsCompulsory)) {
          builder.addMoveStack(newStack)
        }
      }
      if (jumpsCompulsory) {
        processNormalMoves = false
      }
      boardState.pop()
      true
    }

    def tryJumps(moveStack: List[SimpleMove], piece: Piece, from: Int): Boolean = {
      var result = false
      result = tryJump(moveStack, piece, from, forwardMoveW(from), forwardJumpW(from))
      result ||= tryJump(moveStack, piece, from, forwardMoveE(from), forwardJumpE(from))
      if (piece.isKing) {
        result ||= tryJump(moveStack, piece, from, backMoveW(from), backJumpW(from))
        result ||= tryJump(moveStack, piece, from, backMoveE(from), backJumpE(from))
      }
      result
    }

    def tryMove(from: Int, moveNeighbor: js.Array[Int]): Unit = {
      val target = moveNeighbor(from)
      if (target < 0) return
      if (!boardState.isSquareEmpty(target)) return
      builder.addSimpleMove(SimpleMoveIndex(from, target))
    }

    Board.allSquares.foreach { square =>
      val occupant = boardState.getOccupant(square)
      occupant.getPiece.foreach { piece =>
        if (piece.color == turnToMove) {
          tryJumps(Nil, piece, square)
        }
      }
    }

    if (processNormalMoves) {
      Board.allSquares.foreach { square =>
        val occupant = boardState.getOccupant(square)
        occupant.getPiece.foreach { piece =>
          if (piece.color == turnToMove) {
            tryMove(square, forwardMoveW)
            tryMove(square, forwardMoveE)
            if (piece.isKing) {
              tryMove(square, backMoveW)
              tryMove(square, backMoveE)
            }
          }
        }
      }
    }

    builder.result
  }
}