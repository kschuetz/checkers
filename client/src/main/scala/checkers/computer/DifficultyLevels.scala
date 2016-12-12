package checkers.computer

object DifficultyLevels {
  val LevelCount = 12

//  lazy val phase1MaxKCycles: Vector[Int] = Vector(50, 75, 75, 200, 200, 200, 400, 400, 800, 800, 1600, 3200, 4000)
//  lazy val phase2MaxKCycles: Vector[Int] = Vector(50, 200, 300, 400, 500, 1000, 1600, 2000, 2400, 3000, 4000, 6000, 10000)
//  lazy val mainMaxKCycles: Vector[Int] = Vector(75, 200, 300, 500, 1000, 2000, 4000, 8000, 12000, 16000, 20000, 30000, 40000)
  lazy val phase1MaxKCycles: Vector[Int] = Vector(50, 100, 200, 400, 800, 1000, 1300, 1700, 2200, 2800, 3500, 4300, 5200)
  lazy val phase2MaxKCycles: Vector[Int] = Vector(50, 100, 200, 400, 800, 1200, 2000, 3000, 4200, 5600, 7200, 9000, 11000)
  lazy val mainMaxKCycles: Vector[Int] = Vector(50, 100, 200, 400, 800, 1600, 3200, 6400, 12500, 25000, 40000, 70000, 100000)

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
    weight(467, 7, 2),
    weight(600, 7, 2),
    weight(800, 7, 2),
    weight(933, 6, 2),
    weight(1100, 6, 2),
    weight(1267, 6, 2),
    weight(1400, 5, 2),
    weight(1550, 5, 2),
    weight(1666, 4, 2),
    weight(1800, 4, 2),
    weight(2000, 3, 1)
  )

  lazy val mainWeights: Vector[MoveSelectionMethodWeights] = Vector(
    weight(200, 5, 2),
    weight(467, 5, 2),
    weight(600, 5, 2),
    weight(800, 5, 2),
    weight(933, 5, 2),
    weight(1100, 5, 2),
    weight(1267, 4, 2),
    weight(1400, 4, 2),
    weight(1550, 4, 2),
    weight(1666, 3, 2),
    weight(1800, 3, 2),
    weight(2000, 3, 1)
  )


  private lazy val weight = MoveSelectionMethodWeights.apply _
}