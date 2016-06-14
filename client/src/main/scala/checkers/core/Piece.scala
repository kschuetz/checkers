package checkers.core

sealed trait PieceType

object PieceType {

  case object Man extends PieceType

  case object King extends PieceType

}

sealed trait Occupant {
  def getPiece: Option[Piece]
  def isEmpty: Boolean
  def isMan: Boolean
  def isKing: Boolean
  def code: Int
  def crowned: Occupant
  def foreach[A](f: Piece => A): Unit
}

case object Empty extends Occupant {
  val getPiece = None
  val isEmpty = true
  val isMan = false
  val isKing = false
  val code: Int = 0
  val crowned = Empty
  def foreach[A](f: Piece => A): Unit = { }
}

sealed trait Piece extends Occupant {
  def color: Color
  def pieceType: PieceType
  val getPiece = Some(this)
  val isEmpty = false
  def foreach[A](f: Piece => A): Unit = f(this)
}

case object LightMan extends Piece {
  val color = Light
  val pieceType = PieceType.Man
  val code: Int = 4
  val isMan = true
  val isKing = false
  val crowned = LightKing
}

case object DarkMan extends Piece {
  val color = Dark
  val pieceType = PieceType.Man
  val code: Int = 5
  val isMan = true
  val isKing = false
  val crowned = DarkKing
}

case object LightKing extends Piece {
  val color = Light
  val pieceType = PieceType.King
  val code: Int = 6
  val isMan = false
  val isKing = true
  val crowned = LightKing
}

case object DarkKing extends Piece {
  val color = Dark
  val pieceType = PieceType.King
  val code: Int = 7
  val isMan = false
  val isKing = true
  val crowned = DarkKing
}

class NewOccupant(val code: Int) extends AnyVal {
  def isEmpty = (code & 4) == 0
  def isMan = (code & 6) == 4
  def isKing = (code & 6) == 6
  def isLight = (code & 5) == 4
  def isDark = (code & 5) == 5
  def crowned = if(isMan) {
    if(isDark) NewOccupant.DarkKing
    else NewOccupant.LightKing
  } else this
}

object NewOccupant {
  val Empty = new NewOccupant(0)
  val LightMan = new NewOccupant(4)
  val DarkMan = new NewOccupant(5)
  val LightKing = new NewOccupant(6)
  val DarkKing = new NewOccupant(7)
}