package checkers.core

import scala.scalajs.js.typedarray.Int32Array

trait BoardStack extends MutableBoardState {
  def push(): Unit

  def pop(): Unit
}

class BoardStackImpl(val initialCapacity: Int) extends BoardStack with BoardStateWriteImpl {

  import BoardState.frameSize

  protected var size = frameSize * initialCapacity
  protected var data = new Int32Array(size)
  protected var offset = 0

  private def ensureSize(minSize: Int): Unit = {
    if (minSize > size) {
      val newSize = minSize * 2
      val newData = new Int32Array(newSize)
      var i = 0
      while(i < size) {
        newData(i) = data(i)
        i += 1
      }
      data = newData
      size = newSize
    }
  }

  def push(): Unit = {
    val nextOffset = offset + frameSize
    ensureSize(nextOffset)
    var i = offset
    var j = nextOffset
    while(i < nextOffset) {
      data(j) = data(i)
      i += 1
      j += 1
    }
    offset = nextOffset
  }

  def pop(): Unit = {
    offset = offset - frameSize
    if (offset < 0) offset = 0
  }

  def toImmutable: BoardState = new BoardState(copyFrame)
}

object BoardStack {
  val defaultInitialCapacity = 128

  def apply(initialCapacity: Int = defaultInitialCapacity): BoardStack =
    new BoardStackImpl(initialCapacity)

  def fromBoard(boardState: BoardStateRead): BoardStack = {
    val result = apply(defaultInitialCapacity)
    result.setBoard(boardState)
    result
  }
}