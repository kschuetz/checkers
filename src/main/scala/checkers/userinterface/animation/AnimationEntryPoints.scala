package checkers.userinterface.animation

import checkers.consts._
import checkers.core.Board
import checkers.util.Point

trait AnimationEntryPoints {
  def entryPoint(piece: Occupant, toSquare: Int): Point

  def exitPoint(piece: Occupant, fromSquare: Int): Point
}

object DefaultAnimationEntryPoints extends AnimationEntryPoints {

  private lazy val entryPointA = Board.squareCenter(2) + Point(1, 2)
  private lazy val entryPointB = Board.squareCenter(3) + Point(1, 2)
  private lazy val entryPointC = Board.squareCenter(29) - Point(1, 2)
  private lazy val entryPointD = Board.squareCenter(28) - Point(1, 2)

  def exitPoint(piece: Occupant, fromSquare: Int): Point = {
    if(SIDE(piece) == LIGHT) {
      if(Board.isWestSquare(fromSquare)) entryPointA else entryPointB
    } else {
      if(Board.isWestSquare(fromSquare)) entryPointD else entryPointC
    }
  }

  def entryPoint(piece: Occupant, toSquare: Int): Point = {
    if(SIDE(piece) == DARK) {
      if(Board.isWestSquare(toSquare)) entryPointA else entryPointB
    } else {
      if(Board.isWestSquare(toSquare)) entryPointD else entryPointC
    }
  }
}