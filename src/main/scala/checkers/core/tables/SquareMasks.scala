package checkers.core.tables

import scala.scalajs.js.typedarray.Int32Array

object SquareMasks {
  val valueFor: Int32Array = {
    val result = new Int32Array(32)
    for (i <- 0 to 31) {
      result(i) = create(i)
    }
    result
  }

  def createFromSeq(squares: Seq[Int]): Int = {
    var result = 0
    squares.foreach { s => result |= (1 << s) }
    result
  }

  def create(squares: Int*): Int = createFromSeq(squares)

}
