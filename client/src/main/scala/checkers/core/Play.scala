package checkers.core

case class Play(move: List[Int], proposeDraw: Boolean, acceptDraw: Boolean)

object Play {
  val empty = Play(Nil, proposeDraw=false, acceptDraw=false)

  def move(path: List[Int]) = Play(path, proposeDraw=false, acceptDraw=false)
}