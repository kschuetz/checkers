package checkers.test

import checkers.core.{BoardState, BoardStateRead}

object BoardUtils {

  def boardStatesEqual(b1: BoardStateRead, b2: BoardStateRead): Boolean = {
    val frame1 = BoardState.createFrame
    b1.copyFrameTo(frame1, 0)

    val frame2 = BoardState.createFrame
    b2.copyFrameTo(frame2, 0)

    var i = 0
    var result = true
    while(result && i < BoardState.frameSize) {
      if(frame1(i) == frame2(i)) i += 1
      else result = false
    }
    result
  }

}