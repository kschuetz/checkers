package checkers.computer

object DifficultyLevels {
  val LevelCount = 12

  lazy val phase1MaxKCycles: Vector[Int] = Vector(50, 50, 50, 100, 100, 100, 200, 200, 400, 400, 800, 1600, 2000)
  lazy val phase2MaxKCycles: Vector[Int] = Vector(50, 100, 150, 200, 250, 500, 800, 1000, 1200, 1500, 2000, 3000, 5000)
  lazy val mainMaxKCycles: Vector[Int] = Vector(50, 100, 150, 250, 500, 1000, 2000, 4000, 6000, 8000, 10000, 15000, 20000)

  lazy val phase1Weights: Vector[MoveSelectionMethodWeights] = Vector(
    weight(20, 5, 1),
    weight(22, 5, 1),
    weight(24, 5, 1),
    weight(26, 5, 1),
    weight(28, 5, 1),
    weight(30, 5, 1),
    weight(32, 5, 1),
    weight(34, 5, 1),
    weight(36, 5, 1),
    weight(38, 5, 1),
    weight(40, 5, 1),
    weight(42, 5, 1)
  )

  lazy val phase2Weights: Vector[MoveSelectionMethodWeights] = Vector(
    weight(20, 7, 2),
    weight(22, 7, 2),
    weight(24, 7, 2),
    weight(26, 7, 2),
    weight(28, 7, 2),
    weight(30, 7, 2),
    weight(32, 7, 2),
    weight(34, 7, 2),
    weight(36, 7, 2),
    weight(38, 7, 2),
    weight(40, 7, 2),
    weight(42, 7, 2)
  )

  lazy val mainWeights: Vector[MoveSelectionMethodWeights] = Vector(
    weight(50, 8, 2),
    weight(52, 8, 2),
    weight(56, 8, 2),
    weight(60, 7, 2),
    weight(66, 7, 2),
    weight(74, 7, 2),
    weight(84, 7, 2),
    weight(96, 6, 2),
    weight(110, 6, 2),
    weight(126, 6, 2),
    weight(144, 5, 2),
    weight(164, 4, 1)
  )


  private lazy val weight = MoveSelectionMethodWeights.apply _
}