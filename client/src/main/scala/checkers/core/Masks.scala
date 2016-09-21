package checkers.core

import scala.scalajs.js.typedarray.Int32Array

object masks {
  val squares = {
    val result = new Int32Array(32)
    for (i <- 0 to 31) {
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
  val outer = squareMask(0, 1, 2, 3, 7, 8, 15, 16, 23, 24, 28, 29, 30, 31)



  //******

  val crownLight = squareMaskFromSeq(0 to 3)
  val crownDark = squareMaskFromSeq(28 to 31)

  val nw3 = squareMask(4, 5, 6, 12, 13, 14, 20, 21, 22, 28, 29, 30)
  val nw4 = squareMask(8, 9, 10, 11, 16, 17, 18, 19, 24, 25, 26, 27)

  val ne4 = squareMask(4, 5, 6, 7, 12, 13, 14, 15, 20, 21, 22, 23, 28, 29, 30, 31)
  val ne5 = squareMask(9, 10, 11, 17, 18, 19, 25, 26, 27)

  val sw4 = squareMask(0, 1, 2, 3, 8, 9, 10, 11, 16, 17, 18, 19, 24, 25, 26, 27)
  val sw5 = squareMask(4, 5, 6, 12, 13, 14, 20, 21, 22)

  val se3 = squareMask(1, 2, 3, 9, 10, 11, 17, 18, 19, 25, 26, 27)
  val se4 = squareMask(4, 5, 6, 7, 12, 13, 14, 15, 20, 21, 22, 23)


  @inline def shiftNW(board: Int): Int = ((board << 3) & nw3) | ((board << 4) & nw4)

  @inline def shiftNE(board: Int): Int = ((board << 4) & ne4) | ((board << 5) & ne5)

  @inline def shiftSW(board: Int): Int = ((board >> 4) & sw4) | ((board >> 5) & sw5)

  @inline def shiftSE(board: Int): Int = ((board >> 3) & se3) | ((board >> 4) & se4)


  def squareMaskFromSeq(squares: Seq[Int]): Int = {
    var result = 0
    squares.foreach { s => result |= (1 << s) }
    result
  }

  def squareMask(squares: Int*): Int = squareMaskFromSeq(squares)

}
