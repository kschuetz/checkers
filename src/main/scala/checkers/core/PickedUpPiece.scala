package checkers.core

import checkers.consts._
import checkers.util.Point

case class PickedUpPiece(piece: Occupant,
                         parentSquare: Int,
                         movePos: Point)