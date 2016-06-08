package checkers.game

import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint32Array

trait BoardStateRead {
  protected def data: Uint32Array

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
    val code = (data(bank).asInstanceOf[Int] >>> (idx * 3)) & 7
    BoardState.decode(code)
  }

  def copyData: Uint32Array = {
    val result = new Uint32Array(4)
    result(0) = data(0)
    result(1) = data(1)
    result(2) = data(2)
    result(3) = data(3)
    result
  }
}

trait MutableBoardState extends BoardStateRead {
  def setOccupant(squareIndex: Int, value: Occupant): Unit

  def toImmutable: BoardState
}

class BoardState protected[game](protected val data: Uint32Array) extends BoardStateRead {
  def updateMany(piece: Occupant)(indices: Seq[Int]): BoardState = {
    val mb = new MutableState(copyData)
    indices.foreach { idx =>
      mb.setOccupant(idx, piece)
    }
    new BoardState(mb.data)
  }

  def updated(squareIndex: Int, piece: Occupant): BoardState = {
    if(getOccupant(squareIndex) == piece) this
    else {
      val mb = new MutableState(copyData)
      mb.setOccupant(squareIndex, piece)
      new BoardState(mb.data)
    }
  }

  def toMutable: MutableBoardState = new MutableState(copyData)

  private class MutableState(val data: Uint32Array) extends MutableBoardState {
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
      val complement = ~(7 << idx)
      val code = value.code << idx
      data(bank) = (data(bank).asInstanceOf[Int] & complement) | code
    }

    def toImmutable: BoardState = new BoardState(copyData)
  }
}



object BoardState {
  val empty = new BoardState(new Uint32Array(4))

  val decode = js.Array[Occupant](Empty, Empty, Empty, Empty, LightMan, DarkMan, LightKing, DarkKing)
}
