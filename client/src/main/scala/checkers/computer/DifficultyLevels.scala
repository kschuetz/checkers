package checkers.computer

object DifficultyLevels {
  val LevelCount = 12

  lazy val phase1MaxKCycles: Vector[Int] = Vector(50, 100, 200, 400, 800, 1000, 1300, 1700, 2200, 2800, 3500, 4300)
  lazy val phase2MaxKCycles: Vector[Int] = Vector(50, 100, 200, 400, 800, 1200, 2000, 3000, 4200, 5600, 7200, 9000)
  lazy val mainMaxKCycles: Vector[Int] = Vector(50, 100, 200, 400, 800, 1600, 3200, 6400, 12500, 25000, 40000, 70000)

  lazy val phase1Weights: Vector[MoveSelectionMethodWeights] = Vector(
    weight(100, 5, 1),
    weight(133, 5, 1),
    weight(200, 5, 1),
    weight(267, 5, 1),
    weight(433, 5, 1),
    weight(567, 5, 1),
    MoveSelectionMethodWeights.alwaysBestMove,
    MoveSelectionMethodWeights.alwaysBestMove,
    MoveSelectionMethodWeights.alwaysBestMove,
    MoveSelectionMethodWeights.alwaysBestMove,
    MoveSelectionMethodWeights.alwaysBestMove,
    MoveSelectionMethodWeights.alwaysBestMove
  )

  lazy val phase2Weights: Vector[MoveSelectionMethodWeights] = Vector(
    weight(200, 7, 2),
    weight(467, 7, 2),
    weight(600, 7, 2),
    weight(800, 7, 2),
    weight(933, 6, 2),
    weight(1100, 6, 2),
    MoveSelectionMethodWeights.alwaysBestMove,
    MoveSelectionMethodWeights.alwaysBestMove,
    MoveSelectionMethodWeights.alwaysBestMove,
    MoveSelectionMethodWeights.alwaysBestMove,
    MoveSelectionMethodWeights.alwaysBestMove,
    MoveSelectionMethodWeights.alwaysBestMove
  )

  lazy val mainWeights: Vector[MoveSelectionMethodWeights] = Vector(
    weight(200, 5, 2),
    weight(467, 5, 2),
    weight(600, 5, 2),
    weight(800, 5, 2),
    weight(933, 5, 2),
    weight(1100, 5, 2),
    MoveSelectionMethodWeights.alwaysBestMove,
    MoveSelectionMethodWeights.alwaysBestMove,
    MoveSelectionMethodWeights.alwaysBestMove,
    MoveSelectionMethodWeights.alwaysBestMove,
    MoveSelectionMethodWeights.alwaysBestMove,
    MoveSelectionMethodWeights.alwaysBestMove
  )


  private lazy val weight = MoveSelectionMethodWeights.apply _
}