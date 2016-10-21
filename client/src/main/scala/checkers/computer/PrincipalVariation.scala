package checkers.computer

import scala.scalajs.js

class PrincipalVariation[A](val depth: Int) {
  val size = depth * depth
  val line: js.Array[A] = initLine(size)

  def getBestMove(ply: Int): A = line(ply)

  def updateBestMove(ply: Int, value: A): Unit = {
    val baseIndex = ply * depth
    line(baseIndex) = value
    if(ply < depth - 1) {
      var dest = baseIndex + 1
      var source = baseIndex + depth
      var copyCount = depth - ply - 1

      while(copyCount > 0) {
        line(dest) = line(source)
        source += 1
        dest += 1
        copyCount -= 1
      }
    }
  }

//  def updateBestMove(ply: Int, value: A): Unit = {
//    val baseIndex = ply * depth
//    line(baseIndex) = value
//    if(ply > 0) {
//      var source = baseIndex
//      var dest = baseIndex - depth + 1
//      var copyCount = depth - ply - 2
//
//      while(copyCount > 0) {
//        line(dest) = line(source)
//        source += 1
//        dest += 1
//        copyCount -= 1
//      }
//    }
//  }

  private def initLine(size: Int): js.Array[A] = {
    val result = new js.Array[A](size)
    var i = 0
    while(i < size) {
      result(i) = null.asInstanceOf[A]
      i += 1
    }
    result
  }
}