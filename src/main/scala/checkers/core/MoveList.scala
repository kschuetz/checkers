package checkers.core

import scala.scalajs.js.typedarray.Int8Array
import checkers.consts._
import checkers.core.tables.JumpTable


class MoveList(val data: Int8Array,
               val count: Int) {

  def isEmpty: Boolean = count == 0

  def nonEmpty: Boolean = count > 0

  def foreach(f: MoveDecoder => Unit): Unit = {
    val decoder = new MoveDecoder
    var i = 0
    while(i < count) {
      decoder.load(this, i)
      f(decoder)
      i += 1
    }
  }

  def indexOf(path: List[Int], moveDecoder: MoveDecoder = null): Int = {
    var result = -1
    val decoder = if(moveDecoder != null) moveDecoder else new MoveDecoder
    var i = 0
    while(i < count && result < 0) {
      decoder.load(this, i)
      if(decoder.containsPath(path)) result = i
      i += 1
    }
    result
  }

  def containsJump(jumpTable: JumpTable, moveDecoder: MoveDecoder = null): Boolean = {
    if(count > 0) {
      val decoder = if(moveDecoder != null) moveDecoder else new MoveDecoder

      // if any jumps are present, all moves are jumps, so just check index 0
      decoder.load(this, 0)
      decoder.containsJump(jumpTable)
    } else false
  }

  def moveToFrontIfExists(path: List[Int], moveDecoder: MoveDecoder = null): Option[MoveList] = {
    val decoder = if(moveDecoder != null) moveDecoder else new MoveDecoder
    val index = indexOf(path, decoder)
    if(index < 1) None
    else {
      val newData = MoveList.makeBuffer
      MoveList.copyFrame(index, 0, data, newData)
      var from = 0
      var to = 1
      var i = count - 1
      while(i > 0) {
        if(from == index) from += 1
        MoveList.copyFrame(from, to, data, newData)
        from += 1
        to += 1
        i -= 1
      }
      Option(new MoveList(newData, count))
    }
  }

  // for tests
  def toSet: Set[List[Int]] = {
    var result = Set.empty[List[Int]]
    foreach { decoder =>
      result += decoder.pathToList
    }
    result
  }

  def toList: List[List[Int]] = {
    var result = List.empty[List[Int]]
    foreach { decoder =>
      result = decoder.pathToList :: result
    }
    result.reverse
  }

  override def toString: String = s"MoveList(${toList.toString})"
}

/**
  * MoveDecoder:
  *
  * Contains one move.
  * pathLength is the length of the path (2 for simple moves and jumps)
  * data is an array containing the squares in the path.
  *
  * Can be reused by calling load.
  */
class MoveDecoder {
  val data = new Int8Array(MOVELISTFRAMESIZE)
  private var _pathLength = 0

  def load(moveList: MoveList, index: Int): Unit = {
    val src = moveList.data
    var i = index * MOVELISTFRAMESIZE
    _pathLength = 0
    while(_pathLength < MOVELISTFRAMESIZE) {
      val b = src(i)
      if(b < 0) {
        data(_pathLength) = (b & 127).toByte
        i += 1
        _pathLength += 1
      } else return
    }
  }

  def pathLength: Int = _pathLength

  def pathToList: List[Int] = {
    var result = List.empty[Int]
    var i = 0
    while(i < _pathLength) {
      result = data(i) :: result
      i += 1
    }
    result.reverse
  }

  def containsPath(path: List[Int]): Boolean = {
    var result = true
    var p = path
    var i = 0
    while (i < _pathLength && result) {
      val x = data(i)
      p match {
        case Nil => result = false
        case y :: ys =>
          if(x != y) result = false
          else p = ys
      }
      i += 1
    }

    result && p == Nil
  }

  def containsJump(jumpTable: JumpTable): Boolean = {
    (_pathLength > 1) && {
      (_pathLength > 2) || {
        val middle = jumpTable.getMiddle(data(0), data(1))
        middle >= 0
      }
    }
  }

  // for tests
  def loadFromList(path: List[Int]): Unit = {
    var i = 0
    var current = path
    while(current.nonEmpty && i < MOVELISTFRAMESIZE) {
      data(i) = current.head.toByte
      current = current.tail
      i += 1
    }
    _pathLength = i
  }

  def allPaths(moveList: MoveList): List[List[Int]] = {
    var i = 0
    var result = List.empty[List[Int]]
    while(i < moveList.count) {
      load(moveList, i)
      result = pathToList :: result
      i += 1
    }
    result.reverse
  }
}

/**
  * MovePathStack:
  *
  * Used for building compound moves.
  */
class MovePathStack {
  private val data = new Int8Array(MOVELISTFRAMESIZE)
  private var ptr: Int = 0

  def mark: Int = ptr

  def reset(marker: Int): Unit = ptr = marker

  def push(square: Byte): Unit = {
    data(ptr) = (square | 128).asInstanceOf[Byte]
    ptr += 1
  }

  def pop(): Unit = ptr -= 1

  def clear(): Unit = ptr = 0

  def emit(dest: Int8Array, destPtr: Int): Unit = {
    var i = 0
    var j = destPtr
    while (i < ptr) {
      dest(j) = data(i)
      j += 1
      i += 1
    }
  }
}

class MoveListBuilder {
  private val pathSize = MOVELISTFRAMESIZE
  private var data = MoveList.makeBuffer
  private var ptr = 0
  private var count = 0

  def addMove(src: Byte, dest: Byte): Unit = {
    data(ptr) = (src | 128).asInstanceOf[Byte]
    data(ptr + 1) = (dest | 128).asInstanceOf[Byte]
    data(ptr + 2) = 0
    count += 1
    ptr += pathSize
  }

  def addPath(path: MovePathStack): Unit = {
    path.emit(data, ptr)
    count += 1
    ptr += pathSize
  }

  // use only in tests
  def addPathFromList(path: List[Int]): Unit = {
    var p = ptr
    path.foreach { square =>
      data(p) = (128 | square).toByte
      p += 1
    }
    data(p) = 0
    count += 1
    ptr += pathSize
  }

  def result: MoveList = {
    val retval = new MoveList(data, count)
    data = null
    retval
  }
}



object MoveList {
  val maxMoveCount = 36
  val bufferSize: Int = MOVELISTFRAMESIZE * maxMoveCount

  def makeBuffer: Int8Array = new Int8Array(bufferSize)

  def copyFrame(srcFrameIndex: Int, destFrameIndex: Int, srcData: Int8Array, destData: Int8Array): Unit = {
    var from = srcFrameIndex * MOVELISTFRAMESIZE
    var to = destFrameIndex * MOVELISTFRAMESIZE
    var i = MOVELISTFRAMESIZE
    while(i > 0) {
      destData(to) = srcData(from)
      i -= 1
      from += 1
      to += 1
    }
  }

  // used in tests
  def invertPath(path: List[Int]): List[Int] = path.map(square => 31 - square)
}