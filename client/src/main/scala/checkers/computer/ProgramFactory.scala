package checkers.computer

import checkers.core.GameLogicModule

trait ProgramFactory {
  def makeProgram(gameLogicModule: GameLogicModule): Program
}