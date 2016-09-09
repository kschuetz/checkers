package checkers.core

sealed trait Variation {
  def displayName: String
}

object Variation {

  case object Standard extends Variation {
    val displayName = "Standard"
  }

  case object Giveaway extends Variation {
    val displayName = "Giveaway"
  }

}