package checkers.util

object Easing {

  // Domain for all functions is 0..1

  def easeInQuad(t: Double): Double = t * t

  def easeInOutQuart(t: Double): Double = {
    if(t < 0.5) { 8 * t * t * t * t }
    else {
      val x = t - 1
      1 - (8 * x * x * x * x)
    }
  }

}