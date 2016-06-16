package checkers.core

import scala.scalajs.js
import scala.scalajs.js.typedarray.Int32Array
import checkers.consts._
import checkers.util.DebugUtils

trait BoardStateRead {
  def getOccupant(squareIndex: Int): Occupant

  def isSquareEmpty(squareIndex: Int): Boolean

  def squareHasColor(color: Color, squareIndex: Int): Boolean

  def foreach(color: Color)(f: (Int, Occupant) => Unit): Unit

  def copyFrameTo(dest: Int32Array, destIndex: Int = 0): Unit


}

trait MutableBoardState extends BoardStateRead {
  def setOccupant(squareIndex: Int, value: Occupant): Unit

  def setBoard(board: BoardStateRead): Unit

  def toImmutable: BoardStateRead
}


trait BoardStateReadImpl extends BoardStateRead {
  protected def data: Int32Array

  protected def offset: Int

  def getOccupant(squareIndex: Int): Occupant = {
    val k = (data(offset).asInstanceOf[Int] >>> squareIndex) & 1
    val lp = (data(offset + 1).asInstanceOf[Int] >>> squareIndex) & 1
    val dp = (data(offset + 2).asInstanceOf[Int] >>> squareIndex) & 1

    //(k << 2) | (lp << 1) | dp
    if(lp != 0) {
      if(k != 0) LIGHTKING
      else LIGHTMAN
    } else if (dp != 0) {
      if(k != 0) DARKKING
      else DARKMAN
    } else EMPTY
  }

  def isSquareEmpty(squareIndex: Int): Boolean = {
    val p = (data(offset).asInstanceOf[Int] | data(offset + 1).asInstanceOf[Int]) >>> squareIndex
    p == 0
  }

  def squareHasColor(color: Color, squareIndex: Int): Boolean = {
    val lp = (data(offset + 1).asInstanceOf[Int] >>> squareIndex) & 1
    val dp = (data(offset + 2).asInstanceOf[Int] >>> squareIndex) & 1

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
  def setOccupant(squareIndex: Int, value: Occupant): Unit = {
    var k = data(offset).asInstanceOf[Int]
    var lp = data(offset + 1).asInstanceOf[Int]
    var dp = data(offset + 2).asInstanceOf[Int]
    val setMask = BoardState.setMasks(squareIndex).asInstanceOf[Int] >>> 0
    val clearMask = (~setMask) >>> 0


    println(s"setting $squareIndex to ${DebugUtils.occupantToString(value)} $setMask $clearMask")
    println(s"  before: $k  $lp  $dp")

    if(value == LIGHTMAN) {
      k &= clearMask
      lp = (lp | setMask) >>> 0
      dp &= clearMask
    } else if (value == DARKMAN) {
      k &= clearMask
      lp &= clearMask
      dp = (dp | setMask) >>> 0
    } else if (value == LIGHTKING) {
      k  = (k | setMask) >>> 0
      lp  = (lp | setMask) >>> 0
      dp &= clearMask
    }else if (value == DARKKING) {
      k  = (k | setMask) >>> 0
      lp &= clearMask
      dp  = (dp | setMask) >>> 0
    } else {
      k &= clearMask
      lp &= clearMask
      dp &= clearMask
    }

    println(s"  after: $k  $lp  $dp")

    data(offset) = k
    data(offset + 1) = lp
    data(offset + 2) = dp
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

//  val decode = js.Array[Occupant](EMPTY, EMPTY, EMPTY, EMPTY, LIGHTMAN, DARKMAN, LIGHTKING, DARKKING)

  //val piece = js.Array[Occupant](null, null, null, null, LightMan, DarkMan, LightKing, DarkKing)

//  val codeIsEmpty = js.Array[Boolean](true, true, true, true, false, false, false, false)

//  val codeIsLight = js.Array[Boolean](false, false, false, false, true, false, true, false)

//  val codeIsDark = js.Array[Boolean](false, false, false, false, false, true, false, true)

  val setMasks = {
    val s = new Int32Array(32)
    for(i <- 0 to 31) {
      s(i) = (1 << i) >>> 0
    }
    println(s)
    s
  }
}
