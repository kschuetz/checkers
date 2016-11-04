package checkers.core

import scala.scalajs.js.typedarray.Int32Array

trait BoardStack extends MutableBoardState {
  def push(): Unit

  def pop(): Unit

  def level: Int

  def debugGetAllItems: List[BoardState]
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
    assert(offset >= 0, "popped empty stack")
  }

  def level: Int = offset / frameSize

  def toImmutable: BoardState = new BoardState(copyFrame)

  def debugGetAllItems: List[BoardState] = {
    val saveOffset = offset
    var result = List.empty[BoardState]
    while(offset >= 0) {
      result = toImmutable :: result
      offset -= frameSize
    }
    offset = saveOffset
    result
  }
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

  def juxtaposedDebugString(boardStack: BoardStack): String = {
    val boards = boardStack.debugGetAllItems.toVector
    var longest = 0
    val grid = boards.map { board =>
      val rows = board.renderDebugRows
      val size = rows.size
      if(size > longest) longest = size
      rows
    }
    val transposed = (0 until longest).map { rowIndex =>
      grid.map(_.lift(rowIndex).getOrElse("")).mkString(" ")
    }
    transposed.mkString("\n")
  }
}