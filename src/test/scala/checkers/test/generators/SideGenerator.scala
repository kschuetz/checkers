package checkers.test.generators

import checkers.consts._
import nyaya.gen.Gen

trait SideGenerator {
  lazy val genSide: Gen[Side] = Gen.choose(DARK, LIGHT)
}