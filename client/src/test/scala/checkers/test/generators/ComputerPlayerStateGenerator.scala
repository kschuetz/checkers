package checkers.test.generators

import checkers.computer.ComputerPlayerState
import checkers.util.Random
import nyaya.gen.Gen

trait ComputerPlayerStateGenerator {
  lazy val genComputerPlayerState: Gen[ComputerPlayerState] = for {
    seedHi <- Gen.int
    seedLo <- Gen.int
  } yield ComputerPlayerState(new Random(seedHi, seedLo))
}