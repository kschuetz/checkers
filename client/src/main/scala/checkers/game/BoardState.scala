package checkers.game

import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint32Array


trait BoardStateRead {
  def getOccupant(squareIndex: Int): Occupant
}

trait BoardStateWrite {
  def setOccupant(squareIndex: Int, value: Occupant): Unit
}

trait MutableBoardState extends BoardStateRead with BoardStateWrite {
  def setOccupant(squareIndex: Int, value: Occupant): Unit

  def toImmutable: BoardState
}

trait BoardStack extends MutableBoardState {
  def push(): Unit
  def pop(): Unit
}

trait BoardStateReadImpl extends BoardStateRead {
  protected def data: Uint32Array
  protected def offset: Int

  def getOccupant(squareIndex: Int): Occupant = {
    var idx = squareIndex
    var bank = 0
    if(idx >= 24) {
      idx -= 24
      bank = 3
    } else if (idx >= 16) {
      idx -= 16
      bank = 2
    } else if (idx >= 8) {
      idx -= 8
      bank = 1
    }
    val code = (data(offset + bank).asInstanceOf[Int] >>> (idx * 3)) & 7
    BoardState.decode(code)
  }

  def copyFrame: Uint32Array = {
    val result = new Uint32Array(4)
    result(0) = data(offset)
    result(1) = data(offset + 1)
    result(2) = data(offset + 2)
    result(3) = data(offset + 3)
    result
  }
}

trait BoardStateWriteImpl extends BoardStateReadImpl with BoardStateWrite {
  def setOccupant(squareIndex: Int, value: Occupant): Unit = {
    var idx = squareIndex
    var bank = 0
    if(idx >= 24) {
      idx -= 24
      bank = 3
    } else if (idx >= 16) {
      idx -= 16
      bank = 2
    } else if (idx >= 8) {
      idx -= 8
      bank = 1
    }
    idx = idx * 3
    bank += offset
    val complement = ~(7 << idx)
    val code = value.code << idx
    data(bank) = (data(bank).asInstanceOf[Int] & complement) | code
  }
}


class BoardState protected[game](protected val data: Uint32Array) extends BoardStateReadImpl {
  protected val offset = 0

  def updateMany(piece: Occupant)(indices: Seq[Int]): BoardState = {
    val mb = new MutableState(copyFrame)
    indices.foreach { idx =>
      mb.setOccupant(idx, piece)
    }
    new BoardState(mb.data)
  }

  def updated(squareIndex: Int, piece: Occupant): BoardState = {
    if(getOccupant(squareIndex) == piece) this
    else {
      val mb = new MutableState(copyFrame)
      mb.setOccupant(squareIndex, piece)
      new BoardState(mb.data)
    }
  }

  def toMutable: MutableBoardState = new MutableState(copyFrame)

  private class MutableState(val data: Uint32Array) extends MutableBoardState with BoardStateWriteImpl {
    protected val offset = 0

    def toImmutable: BoardState = new BoardState(copyFrame)
  }
}

class BoardStackImpl(val initialCapacity: Int, initial: BoardStateRead) extends BoardStack with BoardStateWriteImpl {
  private val frameSize = 4
  protected var size = frameSize * initialCapacity
  protected var data = new Uint32Array(size)
  protected var offset = 0

  private def ensureSize(minSize: Int): Unit = {
    if(minSize > size) {
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
    if(offset < 0) offset = 0
  }

  def toImmutable: BoardState = new BoardState(copyFrame)
}



object BoardState {
  val empty = new BoardState(new Uint32Array(4))

  val decode = js.Array[Occupant](Empty, Empty, Empty, Empty, LightMan, DarkMan, LightKing, DarkKing)
}
