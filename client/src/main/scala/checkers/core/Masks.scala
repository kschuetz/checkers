package checkers.core

import scala.scalajs.js.typedarray.Int32Array

object Masks {
  val squares = {
    val result = new Int32Array(32)
    for(i <- 0 to 31) {
      result(i) = makeSquareMask(i)
    }
    result
  }

  val l3 = makeSquareMask(1, 2, 3, 9, 10, 11, 17, 18, 19, 25, 26, 27)
  val l5 = makeSquareMask(4, 5, 6, 12, 13, 14, 20, 21, 22)
  val r3 = makeSquareMask(28, 29, 30, 20, 21, 22, 12, 13, 14, 4, 5, 6)
  val r5 = makeSquareMask(25, 26, 27, 17, 18, 19, 9, 10, 11)
  val top = 0
  val bottom = 0
  val trappedLight = 0
  val trappedDark = 0
  val second = 0
  val seventh = 0
  val edges = 0

  def makeSquareMask(squares: Int*): Int = {
    var result = 0
    squares.foreach { s => result |= (1 << s) }
    result
  }
}
