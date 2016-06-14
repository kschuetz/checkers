package checkers.core

import scala.scalajs.js.typedarray.Uint32Array

trait BoardStack extends MutableBoardState {
  def push(): Unit

  def pop(): Unit
}

class BoardStackImpl(val initialCapacity: Int) extends BoardStack with BoardStateWriteImpl {

  import BoardState.frameSize

  protected var size = frameSize * initialCapacity
  protected var data = new Uint32Array(size)
  protected var offset = 0

  private def ensureSize(minSize: Int): Unit = {
    if (minSize > size) {
      val newSize = minSize * 2
      val newData = new Uint32Array(newSize)
      Array.copy(data, 0, newData, 0, size)
      data = newData
      size = newSize
    }
  }

  def push(): Unit = {
    val nextOffset = offset + frameSize
    ensureSize(nextOffset)
    Array.copy(data, offset, data, nextOffset, frameSize)
    offset = nextOffset
  }

  def pop(): Unit = {
    offset = offset - frameSize
    if (offset < 0) offset = 0
  }

  def toImmutable: BoardStateRead = new BoardState(copyFrame)
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