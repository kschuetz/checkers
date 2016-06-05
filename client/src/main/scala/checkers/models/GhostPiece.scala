package checkers.models

import checkers.game.Piece
import checkers.geometry.Point

case class GhostPiece(piece: Piece,
                      parentSquare: Int,
                      grabOffset: Point,
                      moveOffset: Point)