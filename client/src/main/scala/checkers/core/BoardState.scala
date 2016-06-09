package checkers.core

import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint32Array

trait BoardStateRead {
  def getOccupant(squareIndex: Int): Occupant

  def copyFrameTo(dest: Uint32Array, destIndex: Int = 0): Unit
}

trait MutableBoardState extends BoardStateRead {
  def setOccupant(squareIndex: Int, value: Occupant): Unit

  def setBoard(board: BoardStateRead): Unit

  def toImmutable: BoardStateRead
}


trait BoardStateReadImpl extends BoardStateRead {
  protected def data: Uint32Array

  protected def offset: Int

  def getOccupant(squareIndex: Int): Occupant = {
    var idx = squareIndex
    var bank = 0
    if (idx >= 24) {
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

  def copyFrameTo(dest: Uint32Array, destIndex: Int = 0): Unit = {
    dest(destIndex) = data(offset)
    dest(destIndex + 1) = data(offset + 1)
    dest(destIndex + 2) = data(offset + 2)
    dest(destIndex + 3) = data(offset + 3)
  }

  protected def copyFrame: Uint32Array = {
    val result = new Uint32Array(4)
    copyFrameTo(result)
    result
  }
}

trait BoardStateWriteImpl extends BoardStateReadImpl {
  def setOccupant(squareIndex: Int, value: Occupant): Unit = {
    var idx = squareIndex
    var bank = 0
    if (idx >= 24) {
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

  def setBoard(board: BoardStateRead): Unit = {
    board.copyFrameTo(data, offset)
  }
}


class BoardState protected[core](protected val data: Uint32Array) extends BoardStateReadImpl {
  protected val offset = 0

  def updateMany(piece: Occupant)(indices: Seq[Int]): BoardState = {
    val mb = new MutableState(copyFrame)
    indices.foreach { idx =>
      mb.setOccupant(idx, piece)
    }
    new BoardState(mb.data)
  }

  def updated(squareIndex: Int, piece: Occupant): BoardState = {
    if (getOccupant(squareIndex) == piece) this
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



object BoardState {
  val frameSize = 4

  def createFrame: Uint32Array =
    new Uint32Array(frameSize)

  val empty = new BoardState(createFrame)

  val decode = js.Array[Occupant](Empty, Empty, Empty, Empty, LightMan, DarkMan, LightKing, DarkKing)
}
