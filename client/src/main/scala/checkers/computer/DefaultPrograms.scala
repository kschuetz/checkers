package checkers.computer

object DefaultPrograms {

  object ids {
    val DefaultComputerPlayer = "Computer4"
  }

//
//  def registerAll2(registry: ProgramRegistry): Unit = {
//
//    def register(name: String, uniqueId: String, difficultyLevel: Int, factory: ProgramFactory): Unit = {
//      val entry = ProgramRegistryEntry(name, uniqueId, difficultyLevel, factory)
//      registry.register(entry)
//    }
//
//    register("Computer (Easiest)", ids.TrivialPlayer, 0, new TrivialPlayerFactory)
//
//    val medium = {
//      val params = SearchParameters(None, cycleLimit = Option(1000000), MoveSelectionMethodWeights.alwaysBestMove)
//      val personality = new StaticPersonality(params)
//      new ComputerPlayerFactory(personality)
//    }
//
//    register("Computer (Medium)", ids.Medium, 5, medium)
//  }

  def registerAll(registry: ProgramRegistry): Unit = {

    def register(name: String, uniqueId: String, difficultyLevel: Int, factory: ProgramFactory): Unit = {
      val entry = ProgramRegistryEntry(name, uniqueId, difficultyLevel, factory)
      registry.register(entry)
    }

    import DifficultyLevels._
    for(level <- 0 until DifficultyLevels.LevelCount) {
      val phase1 = ProgressivePhase(phase1MaxCycles(level), phase1Weights(level))
      val phase2 = ProgressivePhase(phase2MaxCycles(level), phase2Weights(level))
      val mainPhase = ProgressivePhase(mainMaxCycles(level), mainWeights(level))
      val personality = new ProgressivePlayer(phase1, phase2, mainPhase)
      val player = new ComputerPlayerFactory(personality)
      val difficultyLevel = level + 1
      val name = s"Computer (Level $difficultyLevel)"
      val id = s"Computer$difficultyLevel"
      register(name, id, difficultyLevel, player)
    }
  }

}