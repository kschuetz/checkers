package checkers.geometry

case class Point(x: Double, y: Double) {
  def +(other: Point): Point = Point(x + other.x, y + other.y)
  def -(other: Point): Point = Point(x - other.x, y - other.y)
}

object Point {
  val origin = Point(0, 0)
}