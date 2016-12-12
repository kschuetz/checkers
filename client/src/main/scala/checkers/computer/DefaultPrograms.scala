package checkers.computer

object DefaultPrograms {

  object ids {
    val DefaultComputerPlayer = "Computer4"
  }

  def registerAll(registry: ProgramRegistry): Unit = {

    def register(name: String, uniqueId: String, difficultyLevel: Int, factory: ProgramFactory): Unit = {
      val entry = ProgramRegistryEntry(name, uniqueId, difficultyLevel, factory)
      registry.register(entry)
    }

    import DifficultyLevels._
    for(level <- 0 until DifficultyLevels.LevelCount) {
      val phase1 = ProgressivePhase(1000 * phase1MaxKCycles(level), phase1Weights(level))
      val phase2 = ProgressivePhase(1000 * phase2MaxKCycles(level), phase2Weights(level))
      val mainPhase = ProgressivePhase(1000 * mainMaxKCycles(level), mainWeights(level))

      println(s"level:  $level   $mainPhase")

      val personality = new ProgressivePlayer(phase1, phase2, mainPhase)
      val player = new ComputerPlayerFactory(personality)
      val difficultyLevel = level + 1
      val name = s"Computer (Level $difficultyLevel)"
      val id = s"Computer$difficultyLevel"
      register(name, id, difficultyLevel, player)
    }
  }

}