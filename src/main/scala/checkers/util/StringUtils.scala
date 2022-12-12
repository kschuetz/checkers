package checkers.util

import scala.util.Try

object StringUtils {
  def safeStringToInt(s: String, default: Int = -1): Int = {
    Try(s.toInt).getOrElse(default)
  }

}