package checkers.core

import checkers.consts._
import checkers.geometry.Point

case class PickedUpPiece(piece: Occupant,
                         parentSquare: Int,
                         movePos: Point)