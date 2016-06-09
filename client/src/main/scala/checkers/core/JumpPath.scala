package checkers.core


object JumpPath {
  case class ValidJumpPath private[JumpPath](path: Vector[Int]) {
    def startSquare = path.head
    def endSquare = path.last
  }

  def apply(path: Seq[Int]): ValidJumpPath = {
    if(path.lengthCompare(2) < 0) throw new IllegalArgumentException("JumpPath length must be >= 2")
    val p = path.toVector
    val len = p.length
    var a = p.head
    var i = 1
    while(i < len) {
      val b = p(i)
      if(getMiddleSquare(a, b).isDefined) a = b
      else throw new IllegalArgumentException("Invalid JumpPath")
      i += 1
    }
    new ValidJumpPath(p)
  }

  def getMiddleSquare(startSquare: Int, endSquare: Int): Option[Int] = {
    require(Board.isLegalSquareIndex(startSquare) && Board.isLegalSquareIndex(endSquare))
    val (a, b) = if (startSquare < endSquare) (startSquare, endSquare)
                 else (endSquare, startSquare)
    val d = b - a
    val ra = a / 4
    val rb = b / 4

    if (rb - ra != 2) return None
    if (d == 9) Some(a + 5)
    else if (d == 7) Some(a + 4)
    else None

  }
}