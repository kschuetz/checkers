package checkers.core

import checkers.consts._

object Piece {
  def crowned(occupant: Occupant): Occupant = if(ISMAN(occupant)) occupant & 2 else occupant
}

