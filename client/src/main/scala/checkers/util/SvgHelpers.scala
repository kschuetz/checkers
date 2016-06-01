package checkers.util

trait SvgHelpers {

  def pointToPathString(point: (Double, Double)): String =
    s"${point._1},${point._2}"

  def pointsToPathString(points: (Double, Double)*): String =
    points.map(pointToPathString).mkString(" ")


}