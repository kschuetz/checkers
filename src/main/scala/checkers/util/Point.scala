package checkers.util


case class Point(x: Double, y: Double) {
  def +(other: Point): Point = Point(x + other.x, y + other.y)
  def -(other: Point): Point = Point(x - other.x, y - other.y)
  def magnitude: Double = math.sqrt(x * x + y * y)
  def /(value: Double): Point = Point(x / value, y / value)
  def dot(other: Point): Double = x * other.x + y * other.y
}

object Point {
  val origin = Point(0, 0)
}