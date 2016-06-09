package checkers.models

import checkers.core.Piece
import checkers.geometry.Point

case class GhostPiece(piece: Piece,
                      parentSquare: Int,
                      grabOffset: Point,
                      movePos: Point)