package checkers.game

import checkers.geometry.Point

sealed trait SceneObject

object SceneObject {


  case class GhostPiece(piece: Piece,
                        parentSquare: Int,
                        grabOffset: Point,
                        moveOffset: Point) extends SceneObject

  case class LastMoveArrow(fromSquare: Int,
                           toSquare: Int) extends SceneObject

}

