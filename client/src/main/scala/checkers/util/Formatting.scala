package checkers.util

object Formatting {

  def clockDisplay(timeSeconds: Int): String = {
    val hours = timeSeconds / 3600
    val remainder = timeSeconds % 3600
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

