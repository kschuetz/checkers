package checkers.core

sealed trait BoardOrientation {
  def angle: Double
  def opposite: BoardOrientation
}

object BoardOrientation {
  case object Normal extends BoardOrientation {
    val angle = 0d
    val opposite = Flipped
  }

  case object Flipped extends BoardOrientation {
    val angle = 180d
    val opposite = Normal
  }
}
