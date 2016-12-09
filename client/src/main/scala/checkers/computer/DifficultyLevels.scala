package checkers.computer

object DifficultyLevels {
  val LevelCount = 12

  lazy val phase1MaxKCycles: Vector[Int] = Vector(50, 50, 50, 100, 100, 100, 200, 200, 400, 400, 800, 1600, 2000)
  lazy val phase2MaxKCycles: Vector[Int] = Vector(50, 100, 150, 200, 250, 500, 800, 1000, 1200, 1500, 2000, 3000, 5000)
  lazy val mainMaxKCycles: Vector[Int] = Vector(50, 100, 150, 250, 500, 1000, 2000, 4000, 6000, 8000, 10000, 15000, 20000)

  lazy val phase1Weights: Vector[MoveSelectionMethodWeights] = Vector(
    weight(100, 5, 1),
    weight(133, 5, 1),
    weight(200, 5, 1),
    weight(267, 5, 1),
    weight(433, 5, 1),
    weight(567, 5, 1),
    weight(633, 4, 1),
    weight(700, 4, 1),
    weight(767, 4, 1),
    weight(833, 3, 1),
    weight(900, 3, 1),
    weight(1000, 3, 1)
  )

  lazy val phase2Weights: Vector[MoveSelectionMethodWeights] = Vector(
    weight(200, 7, 2),
    weight(233, 7, 2),
    weight(300, 7, 2),
    weight(367, 7, 2),
    weight(433, 6, 2),
    weight(567, 6, 2),
    weight(633, 6, 2),
    weight(700, 5, 2),
    weight(767, 5, 2),
    weight(833, 4, 2),
    weight(900, 4, 2),
    weight(1000, 3, 1)
  )

  lazy val mainWeights: Vector[MoveSelectionMethodWeights] = Vector(
    weight(200, 5, 2),
    weight(233, 5, 2),
    weight(300, 5, 2),
    weight(367, 5, 2),
    weight(433, 5, 2),
    weight(567, 5, 2),
    weight(633, 4, 2),
    weight(700, 4, 2),
    weight(767, 4, 2),
    weight(833, 3, 2),
    weight(900, 3, 2),
    weight(1000, 3, 1)
  )


  private lazy val weight = MoveSelectionMethodWeights.apply _
}