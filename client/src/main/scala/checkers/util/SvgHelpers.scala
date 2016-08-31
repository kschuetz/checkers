package checkers.util

import checkers.geometry.Point

trait SvgHelpers {

  def pointToPathString(point: Point): String =
    s"${point.x},${point.y}"

  def pointsToPathString(points: Point*): String =
    points.map(pointToPathString).mkString(" ")

  def pathSegment(command: String, points: Point*) =
    points.map(pointToPathString).mkString(s"$command ", " ", "")

}