package checkers.test.generators

import checkers.consts._
import nyaya.gen.Gen

trait ColorGenerator {
  lazy val genColor: Gen[Color] = Gen.choose(DARK, LIGHT)
}