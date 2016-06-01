package checkers.game

sealed trait Color {
  def opposite: Color
}

case object Dark extends Color {
  val opposite = Light
}

case object Light extends Color {
  val opposite = Dark
}