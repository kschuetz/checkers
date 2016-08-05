package checkers.models

import checkers.consts._
import checkers.geometry.Point

case class PickedUpPiece(piece: Occupant,
                         parentSquare: Int,
                         grabOffset: Point,
                         movePos: Point)