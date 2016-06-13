package checkers.core

import checkers.core.PieceType.King

import scala.scalajs.js


class MoveGenerator(rulesSettings: RulesSettings) {

  type MoveList = Seq[Move]

  private val jumpsCompulsory = true

  def generateMoves(boardState: BoardStack, turnToMove: Color): MoveList = {


    val opponent = turnToMove.opposite
    var processNormalMoves = true
    val builder = new MoveListBuilder

    def squareEmpty(target: Int): Boolean =
      (target >= 0) && boardState.isSquareEmpty(target)

    def hasOpponent(target: Int): Boolean =
      (target >= 0) && boardState.squareHasColor(opponent, target)

    val crowningSquares = Board.crowningSquares(turnToMove)
    val neighborIndex = NeighborIndex.forColor(turnToMove)
    import neighborIndex._

    def tryJump(square: Int, moveNeighbor: js.Array[Int], jumpNeighbor: js.Array[Int]): Unit = {
      val over = moveNeighbor(square)
      val target = jumpNeighbor(square)
      if(over < 0 || target < 0) return
      if(!boardState.isSquareEmpty(target)) return
      if(!boardState.squareHasColor(opponent, over)) return
      // TODO: add jump to move list
    }

    def canJump(square: Int, over: Int, target: Int): Boolean = {
      if(over < 0 || target < 0) return false
      if(!boardState.isSquareEmpty(target)) return false
      if(!boardState.squareHasColor(opponent, over)) return false
      true
    }

    def tryJumps(path: List[Int], isKing: Boolean): Int = {

      def go(square: Int, moveNeighbor: js.Array[Int], jumpNeighbor: js.Array[Int]): Int = {
        val over = moveNeighbor(square)
        val target = jumpNeighbor(square)
        if(canJump(square, over, target)) {
          val newPath = square :: path
          if(jumpsCompulsory) {
            processNormalMoves = false
          }
          if(!isKing && crowningSquares.contains(square)) {

          }
          //builder.addCompoundMove()
          ???
        }
        0  // TODO
      }

      path match {
        case first :: Nil =>
      }
      0
    }

    def tryMove(square: Int, moveNeighbor: js.Array[Int]): Unit = {
      val target = moveNeighbor(square)
      if(target < 0) return
      if(!boardState.isSquareEmpty(target)) return
      builder.addSimpleMove(square, target)
    }

    Board.allSquares.foreach { square =>
      val occupant = boardState.getOccupant(square)
      occupant.getPiece.foreach { piece =>
        if(piece.color == turnToMove) {
          tryJump(square, forwardMoveW, forwardJumpW)
          tryJump(square, forwardMoveE, forwardJumpE)
          if(piece.pieceType == King) {
            tryJump(square, backMoveW, backJumpW)
            tryJump(square, backMoveE, backJumpE)
          }
        }
      }
    }

    if(processNormalMoves) {
      Board.allSquares.foreach { square =>
        val occupant = boardState.getOccupant(square)
        occupant.getPiece.foreach { piece =>
          if(piece.color == turnToMove) {
            tryMove(square, forwardMoveW)
            tryMove(square, forwardMoveE)
            if(piece.pieceType == King) {
              tryMove(square, backMoveW)
              tryMove(square, backMoveE)
            }
          }
        }
      }
    }
    List.empty[Move]
  }



}