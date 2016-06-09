package checkers.core

import checkers.core.PieceType.King

import scala.scalajs.js

sealed trait Move
case class SimpleMove(from: Int, to: Int) extends Move
case class Jump(from: Int, to: Int) extends Move



class MoveGenerator(rulesSettings: RulesSettings) {

  type MoveList = Seq[Move]

  def generateMoves(boardState: BoardStateRead, turnToMove: Color): MoveList = {

    val opponent = turnToMove.opposite
    var processNormalMoves = true

    def squareEmpty(target: Int): Boolean =
      (target >= 0) && boardState.isSquareEmpty(target)

    def hasOpponent(target: Int): Boolean =
      (target >= 0) && boardState.squareHasColor(opponent, target)

    def tryJump(square: Int, moveNeighbor: js.Array[Int], jumpNeighbor: js.Array[Int]): Unit = {
      val over = moveNeighbor(square)
      val target = jumpNeighbor(square)
      if(over < 0 || target < 0) return
      if(!boardState.isSquareEmpty(target)) return
      if(!boardState.squareHasColor(opponent, over)) return
      // TODO: add jump to move list
    }

    def tryMove(square: Int, moveNeighbor: js.Array[Int]): Unit = {
      val target = moveNeighbor(square)
      if(target < 0) return
      if(!boardState.isSquareEmpty(target)) return
      // TODO: add move to move list
    }


    val neighborIndex = turnToMove match {
      case Dark => DarkNeighborIndex
      case Light => LightNeighborIndex
    }
    import neighborIndex._

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