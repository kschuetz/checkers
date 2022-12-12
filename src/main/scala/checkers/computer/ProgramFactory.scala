package checkers.computer

import checkers.core.GameLogicModule

trait ProgramFactory {
  def makeProgram(gameLogicModule: GameLogicModule, initialSeed: Option[Long]): Program
}