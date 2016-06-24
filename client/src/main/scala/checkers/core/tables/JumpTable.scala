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