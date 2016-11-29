package checkers.util

object Formatting {
  def clockDisplay(timeMillis: Double): String = {
    val totalSeconds = math.floor(timeMillis / 1000).toInt
    val hours = totalSeconds / 3600
    val remainder = totalSeconds % 3600
    val minutes = remainder / 60
    val seconds = remainder % 60
    if(hours > 0) {
      f"$hours:$minutes%02d:$seconds%02d"
    } else if(minutes > 0) {
      f"$minutes%d:$seconds%02d"
    } else {
      f"0:$seconds%02d"
    }
  }

}

