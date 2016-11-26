package checkers.computer

object DifficultyLevels {
  val LevelCount = 12

  lazy val phase1MaxKCycles: Vector[Int] = Vector(50, 50, 50, 100, 100, 100, 200, 200, 400, 400, 800, 1600, 2000)
  lazy val phase2MaxKCycles: Vector[Int] = Vector(50, 100, 150, 200, 250, 500, 800, 1000, 1200, 1500, 2000, 3000, 5000)
  lazy val mainMaxKCycles: Vector[Int] = Vector(50, 100, 150, 250, 500, 1000, 2000, 4000, 6000, 8000, 10000, 15000, 20000)

  lazy val phase1Weights: Vector[MoveSelectionMethodWeights] = Vector(
    weight(40, 5, 1),
    weight(42, 5, 1),
    weight(44, 5, 1),
    weight(46, 5, 1),
    weight(48, 5, 1),
    weight(50, 5, 1),
    weight(52, 5, 1),
    weight(54, 5, 1),
    weight(56, 5, 1),
    weight(58, 5, 1),
    weight(60, 5, 1),
    weight(62, 5, 1)
  )

  lazy val phase2Weights: Vector[MoveSelectionMethodWeights] = Vector(
    weight(40, 7, 2),
    weight(42, 7, 2),
    weight(44, 7, 2),
    weight(46, 7, 2),
    weight(48, 7, 2),
    weight(50, 7, 2),
    weight(52, 7, 2),
    weight(54, 7, 2),
    weight(56, 7, 2),
    weight(58, 7, 2),
    weight(60, 7, 2),
    weight(62, 7, 2)
  )

  lazy val mainWeights: Vector[MoveSelectionMethodWeights] = Vector(
    weight(60, 8, 2),
    weight(62, 8, 2),
    weight(66, 8, 2),
    weight(70, 7, 2),
    weight(76, 7, 2),
    weight(84, 7, 2),
    weight(94, 7, 2),
    weight(106, 6, 2),
    weight(120, 6, 2),
    weight(136, 6, 2),
    weight(154, 5, 2),
    weight(174, 4, 1)
  )


  private lazy val weight = MoveSelectionMethodWeights.apply _
}