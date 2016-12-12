package checkers.core

trait InitialSeedsProvider {
  def getInitialSeeds: InitialSeeds
}

case class StaticInitialSeedsProvider(getInitialSeeds: InitialSeeds) extends InitialSeedsProvider

object DefaultInitialSeedsProvider extends StaticInitialSeedsProvider(InitialSeeds.default)