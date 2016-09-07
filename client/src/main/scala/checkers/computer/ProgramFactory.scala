package checkers.computer

import checkers.core.GameLogicModule

trait ProgramFactory[S] {
  def makeProgram(gameLogicModule: GameLogicModule): Program[S]
}