package checkers.util

import org.scalajs.dom

import scala.scalajs.js


object DebugUtils {

  @inline
  def log(msg: js.Any) = dom.console.log(msg)

}