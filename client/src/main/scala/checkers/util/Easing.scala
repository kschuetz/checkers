package checkers.util

object Easing {

  def easeInQuad(t: Double): Double = t * t

  def easeInOutQuart(t: Double): Double = {
    var x = t * 2
    if(x < 0.5) { 0.5 * x * x * x * x }
    else {
      x -= 2
      -0.5 * (x * x * x * x - 2)
    }
  }

}