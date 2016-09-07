package checkers.computer

object DefaultPrograms {

  def registerAll(registry: ProgramRegistry): Unit = {

    def register(name: String, uniqueId: String, difficultyLevel: Int, factory: ProgramFactory[_]): Unit = {
      val entry = ProgramRegistryEntry(name, uniqueId, difficultyLevel, factory)
      registry.register(entry)
    }

    register("Computer (Easiest)", "TrivialPlayer", 0, new TrivialPlayerFactory)
  }

}