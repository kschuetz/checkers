package checkers.core

import checkers.consts._
import checkers.core.Variation.Giveaway
import upickle.default._

case class RulesSettings(playsFirst: Side,
                         variation: Variation) {
  val giveaway: Boolean = variation == Giveaway
}


object RulesSettings {

  val default = RulesSettings(
    playsFirst = DARK,
    variation = Variation.Standard)

  implicit val rw: ReadWriter[RulesSettings] = macroRW
}
