package checkers.util

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSName

@JSName("Map")
@js.native
class NativeMap[K, V] extends js.Object {
  def get(key: K): UndefOr[V] = js.native

  def set(key: K, value: V): Unit = js.native
}