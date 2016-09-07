package checkers.computer

object DefaultPrograms {

  object ids {
    val TrivialPlayer = "TrivialPlayer"
  }


  def registerAll(registry: ProgramRegistry): Unit = {

    def register(name: String, uniqueId: String, difficultyLevel: Int, factory: ProgramFactory): Unit = {
      val entry = ProgramRegistryEntry(name, uniqueId, difficultyLevel, factory)
      registry.register(entry)
    }

    register("Computer (Easiest)", ids.TrivialPlayer, 0, new TrivialPlayerFactory)
  }

}