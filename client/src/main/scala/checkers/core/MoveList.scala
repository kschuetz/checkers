package checkers.core

import scala.scalajs.js.typedarray.Int8Array


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

  def moveToFrontIfExists(path: List[Int], moveDecoder: MoveDecoder = null): MoveList = {
    val decoder = if(moveDecoder != null) moveDecoder else new MoveDecoder
    val index = indexOf(path, moveDecoder)
    if(index < 1) this
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
      new MoveList(newData, count)
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

  override def toString: String = s"MoveList(${toSet.toString})"
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
  val data = new Int8Array(MoveList.frameSize)
  private var _pathLength = 0

  def load(moveList: MoveList, index: Int): Unit = {
    val src = moveList.data
    var i = index * MoveList.frameSize
    _pathLength = 0
    while(_pathLength < MoveList.frameSize) {
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
        case Nil => false
        case y :: ys =>
          if(x != y) result = false
          else p = ys
      }
      i += 1
    }

    result && p == Nil
  }

  // for tests
  def loadFromList(path: List[Int]): Unit = {
    var i = 0
    var current = path
    while(current.nonEmpty && i < MoveList.frameSize) {
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
  private val data = new Int8Array(MoveList.frameSize)
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
  private val pathSize = MoveList.frameSize
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

  def result: MoveList = {
    val retval = new MoveList(data, count)
    data = null
    retval
  }
}



object MoveList {
  val frameSize = 12
  val maxMoveCount = 36
  val bufferSize = frameSize * maxMoveCount

  def makeBuffer: Int8Array = new Int8Array(bufferSize)

  def copyFrame(srcFrameIndex: Int, destFrameIndex: Int, srcData: Int8Array, destData: Int8Array): Unit = {
    var from = srcFrameIndex * frameSize
    var to = destFrameIndex * frameSize
    var i = frameSize
    while(i > 0) {
      destData(to) = srcData(from)
      i -= 1
      from += 1
      to += 1
    }
  }
}