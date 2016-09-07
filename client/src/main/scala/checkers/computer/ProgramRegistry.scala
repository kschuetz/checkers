package checkers.computer

import checkers.core.{Computer, GameLogicModule}

case class ProgramRegistryEntry(name: String,
                                uniqueId: String,
                                difficultyLevel: Int,
                                factory: ProgramFactory) {
  def makeComputerPlayer(gameLogicModule: GameLogicModule): Computer = {
    val program = factory.makeProgram(gameLogicModule)
    Computer(program = program,
      displayName = name,
      programId = Some(uniqueId),
      difficultyLevel = difficultyLevel)
  }

}


class ProgramRegistry {
  private var _entries: Vector[ProgramRegistryEntry] = Vector.empty

  def entries: Vector[ProgramRegistryEntry] = _entries

  def register(entry: ProgramRegistryEntry): Unit =
    _entries = _entries :+ entry

  def findEntry(id: String): Option[ProgramRegistryEntry] =
    _entries.find(_.uniqueId == id)

}