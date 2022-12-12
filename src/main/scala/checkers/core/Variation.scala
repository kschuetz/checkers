package checkers.core

import upickle.default._

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

  lazy val all = Vector(Standard, Giveaway)

  val default = Standard

  implicit val rwStandard: ReadWriter[Standard.type] = macroRW
  implicit val rwGiveaway: ReadWriter[Giveaway.type] = macroRW
  implicit val rw: ReadWriter[Variation] = macroRW

}
