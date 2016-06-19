package checkers.core

import checkers.consts._

import scala.scalajs.js.typedarray.Int32Array

trait BoardStateRead {
  def lightPieces: Int

  def darkPieces: Int

  def kings: Int

  def getOccupant(squareIndex: Int): Occupant

  def isSquareEmpty(squareIndex: Int): Boolean

  def squareHasColor(color: Color, squareIndex: Int): Boolean

  def foreach(color: Color)(f: (Int, Occupant) => Unit): Unit

  def copyFrameTo(dest: Int32Array, destIndex: Int = 0): Unit


}

trait MutableBoardState extends BoardStateRead {
  def setLightPieces(value: Int): Unit

  def setDarkPieces(value: Int): Unit

  def setKings(value: Int): Unit

  def setOccupant(squareIndex: Int, value: Occupant): Unit

  def setBoard(board: BoardStateRead): Unit

  def toImmutable: BoardStateRead
}


trait BoardStateReadImpl extends BoardStateRead {
  protected def data: Int32Array

  protected def offset: Int

  override def kings: PieceType = data(offset)

  override def lightPieces: PieceType = data(offset + 1)

  override def darkPieces: PieceType = data(offset + 2)

  def getOccupant(squareIndex: Int): Occupant = {
    val k = (kings >>> squareIndex) & 1
    val lp = (lightPieces >>> squareIndex) & 1
    val dp = (darkPieces >>> squareIndex) & 1

    (k << 2) | (lp << 1) | dp
  }

  def isSquareEmpty(squareIndex: Int): Boolean = {
    val p = (lightPieces | darkPieces) >>> squareIndex
    (p & 1) == 0
  }

  def squareHasColor(color: Color, squareIndex: Int): Boolean = {
    val lp = (lightPieces >>> squareIndex) & 1
    val dp = (darkPieces >>> squareIndex) & 1

    COLOR((lp << 1) | dp) == color
  }

  def foreach(color: Color)(f: (Int, Occupant) => Unit): Unit = {
    var i = 0
    while(i < 31) {
      val code = getOccupant(i)
      if(COLOR(code) == color) { f(i, code) }
      i += 1
    }
  }

  def copyFrameTo(dest: Int32Array, destIndex: Int = 0): Unit = {
    dest(destIndex) = data(offset)
    dest(destIndex + 1) = data(offset + 1)
    dest(destIndex + 2) = data(offset + 2)
  }

  protected def copyFrame: Int32Array = {
    val result = new Int32Array(3)
    copyFrameTo(result)
    result
  }
}

trait BoardStateWriteImpl extends BoardStateReadImpl {
  def setLightPieces(value: Int): Unit = {
    data(offset + 1) = value
  }

  def setDarkPieces(value: Int): Unit = {
    data(offset + 2) = value
  }

  def setKings(value: Int): Unit = {
    data(offset) = value
  }

  def setOccupant(squareIndex: Int, value: Occupant): Unit = {
    var k = kings
    var lp = lightPieces
    var dp = darkPieces
    val setMask = masks.squares(squareIndex)
    val clearMask = ~setMask

    if(value == LIGHTMAN) {
      k &= clearMask
      lp |= setMask
      dp &= clearMask
    } else if (value == DARKMAN) {
      k &= clearMask
      lp &= clearMask
      dp |= setMask
    } else if (value == LIGHTKING) {
      k |= setMask
      lp |= setMask
      dp &= clearMask
    } else if (value == DARKKING) {
      k |= setMask
      lp &= clearMask
      dp |= setMask
    } else {
      k &= clearMask
      lp &= clearMask
      dp &= clearMask
    }

    setKings(k)
    setLightPieces(lp)
    setDarkPieces(dp)
  }

  def setBoard(board: BoardStateRead): Unit = {
    board.copyFrameTo(data, offset)
  }
}


class BoardState protected[core](val data: Int32Array) extends BoardStateReadImpl {
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

  private class MutableState(val data: Int32Array) extends MutableBoardState with BoardStateWriteImpl {
    protected val offset = 0

    def toImmutable: BoardState = new BoardState(copyFrame)
  }
}



object BoardState {
  val frameSize = 3

  def createFrame: Int32Array =
    new Int32Array(frameSize)

  val empty = new BoardState(createFrame)


}
