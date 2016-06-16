package checkers.util

import checkers.consts._
import checkers.core.BoardStateRead
import org.scalajs.dom

import scala.scalajs.js


object DebugUtils {

  @inline
  def log(msg: js.Any) = dom.console.log(msg)

  def occupantToString(value: Occupant): String =
    if(value == LIGHTMAN) "LIGHTMAN"
    else if(value == DARKMAN) "DARKMAN"
    else if(value == LIGHTKING) "LIGHTKING"
    else if(value == DARKKING) "DARKKING"
    else if(value == EMPTY) "EMPTY"
    else value.toString


  def printOccupants(boardState: BoardStateRead): Unit = {
    val output = (0 to 31).map(idx => occupantToString(boardState.getOccupant(idx))).toVector
    println(output)
  }

}