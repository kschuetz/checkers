package checkers.core

import scala.scalajs.js.typedarray.Int32Array

object masks {
  val squares = {
    val result = new Int32Array(32)
    for(i <- 0 to 31) {
      result(i) = squareMask(i)
    }
    result
  }

  val l3 = squareMask(1, 2, 3, 9, 10, 11, 17, 18, 19, 25, 26, 27)
  val l5 = squareMask(4, 5, 6, 12, 13, 14, 20, 21, 22)
  val r3 = squareMask(28, 29, 30, 20, 21, 22, 12, 13, 14, 4, 5, 6)
  val r5 = squareMask(25, 26, 27, 17, 18, 19, 9, 10, 11)
  val top = squareMaskFromSeq(20 to 31)
  val bottom = squareMaskFromSeq(0 to 11)
  val trappedLight = squareMaskFromSeq(0 to 4)
  val trappedDark = squareMaskFromSeq(27 to 31)
  val second = squareMaskFromSeq(4 to 7)
  val seventh = squareMaskFromSeq(24 to 27)
  val edges = squareMask(24, 20, 16, 12, 8, 4, 0, 7, 11, 15, 19, 23, 27, 31)

  def squareMaskFromSeq(squares: Seq[Int]): Int = {
    var result = 0
    squares.foreach { s => result |= (1 << s) }
    result
  }

  def squareMask(squares: Int*): Int = squareMaskFromSeq(squares)

}
