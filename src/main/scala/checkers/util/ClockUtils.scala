package checkers.util

object ClockUtils {
  def toSeconds(timeMillis: Double): Int = math.floor(timeMillis / 1000).toInt
}