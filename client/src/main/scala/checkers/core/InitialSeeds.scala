package checkers.core

case class InitialSeeds(darkPlayer: Option[Long],
                        lightPlayer: Option[Long],
                        darkMentor: Option[Long],
                        lightMentor: Option[Long])


object InitialSeeds {
  val default: InitialSeeds = InitialSeeds(None, None, None, None)
}