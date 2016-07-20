package checkers.core

import checkers.consts._
import checkers.core.tables.JumpTable
import checkers.models.GameModel

// TODO: think of a better name for PlayExecutor

sealed trait PlayEvent

object PlayEvent {
  case object NoEvent extends PlayEvent
  case object DrawProposed extends PlayEvent
  case object DrawAccepted extends PlayEvent
}

class PlayExecutor(jumpTable: JumpTable,
                   drawLogic: DrawLogic,
                   moveExecutor: MoveExecutor,
                   animationPlanner: AnimationPlanner) {

  def applyPlay[DS, LS](gameModel: GameModel[DS, LS], play: Play): Option[(PlayEvent, GameModel[DS, LS])] = {
    val myself = gameModel.turnToMove
    val opponent = OPPONENT(myself)
    val gameState = gameModel.gameState

    play match {
      case Play.NoPlay => None

      case Play.AcceptDraw =>
        if(drawLogic.canAcceptDraw(gameState)) {
          val newState = gameState.acceptDraw
          val newModel = gameModel.copy(gameState = newState, phase = Phase.GameOver(None))
          Some((PlayEvent.DrawAccepted, newModel))

        } else None

      case Play.Move(path, proposeDraw) =>
        val boardState = gameState.board.toMutable

        def go(path: List[Int], result: List[MoveInfo]): List[MoveInfo] = {
          path match {
            case Nil => result
            case from :: (more@(to :: _)) =>
              val info = moveExecutor.execute(boardState, from, to)
              go(more, info :: result)
          }
        }
        val moveInfo = go(path, Nil)
        val newBoard = boardState.toImmutable
        ???
    }

  }

  //private def acceptDraw[DS, LS](gameModel: GameModel)

//  def applyPartialPlay[DS, LS](gameState: GameState[DS, LS]): (PlayEvent, GameState[DS, LS]) = {
//    ???
//  }
//  /**
//    * Updates the board in place.  Does not return metadata, other than a flag indicating a crowning event.
//    * @return if true, move ended in a piece being crowned
//    */
//  def fastExecute(boardState: MutableBoardState, from: Int, to: Int): Boolean = {
//    var crowned = false
//    val piece = boardState.getOccupant(from)
//    val over = jumpTable.getMiddle(from, to)
//    if(over >= 0) boardState.setOccupant(over, EMPTY)
//
//    boardState.setOccupant(from, EMPTY)
//
//    val m = 1 << to
//    if(piece == LIGHTMAN && (m & masks.crownLight) != 0) {
//      crowned = true
//      boardState.setOccupant(to, LIGHTKING)
//    } else if (piece == DARKMAN && (m & masks.crownDark) != 0) {
//      crowned = true
//      boardState.setOccupant(to, DARKKING)
//      crowned = true
//    } else {
//      boardState.setOccupant(to, piece)
//    }
//
//    crowned
//  }

  // TODO: apply Play
  // TODO: apply partial move
}