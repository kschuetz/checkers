package checkers.util

import scala.scalajs.js.typedarray.Int8Array

object Shuffle {
  def shuffleSlice(data: Int8Array, startIndex: Int, endIndexExcl: Int, stateIn: Random): Random = {
    var state = stateIn
    var i = startIndex
    val m = endIndexExcl - 1
    while (i < m) {
      val (n, nextState) = state.nextInt(m - i)
      state = nextState
      val j = i + n
      val temp = data(j)
      data(j) = data(i)
      data(i) = temp
      i += 1
    }

    state
  }
}