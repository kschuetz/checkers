package checkers.core.tables

import scala.scalajs.js.typedarray.Int8Array


class JumpTable(neighborTable: NeighborTable) {
  val data = {
    val result = new Int8Array(1024)
    JumpTable.init(result, neighborTable)
    result
  }

  def getMiddle(from: Int, to: Int): Int = {
    val code = (from << 5) | (to & 31)
    val over = data(code)
    if(over < 0) over & 127
    else -1
  }

  def getMiddles(path: List[Int]): List[Int] = {
    val initial = (-1, List.empty[Int])
    val (_, result) = path.foldLeft(initial) { case ((from, acc), to) =>
      val next = if(from >= 0) {
        val middle = getMiddle(from, to)
        if(middle >= 0) middle :: acc else acc
      } else acc
      (to, next)
    }
    result.reverse
  }

  def isJump(path: List[Int]): Boolean = path match {
    case Nil => false
    case _ :: Nil => false
    case from :: to :: _ => getMiddle(from, to) >= 0
  }
}

object JumpTable {
  private[tables] def init(data: Int8Array, neighborIndex: NeighborTable): Unit = {
    def addJump(from: Int, to: Int, over: Int): Unit = {
      if(from >= 0 && to >= 0 && over >= 0) {
        val encodeOver = (128 | (over & 127)).toByte
        val code1 = (from << 5) | (to & 31)
        data(code1) = encodeOver
        val code2 = (to << 5) | (from & 31)
        data(code2) = encodeOver
      }
    }
    for(square <- 0 to 31) {
      addJump(neighborIndex.moveSE(square), neighborIndex.moveNW(square), square)
      addJump(neighborIndex.moveSW(square), neighborIndex.moveNE(square), square)
    }
  }
}