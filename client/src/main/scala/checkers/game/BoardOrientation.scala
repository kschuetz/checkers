package checkers.game

sealed trait BoardOrientation

object BoardOrientation {
  case object Normal extends BoardOrientation

  case object Flipped extends BoardOrientation
}

