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
}

case object Empty extends Occupant {
  val getPiece = None
  val isEmpty = true
  val isMan = false
  val isKing = false
  val code: Int = 0
  val crowned = Empty
}

sealed trait Piece extends Occupant {
  def color: Color
  def pieceType: PieceType
  val getPiece = Some(this)
  val isEmpty = false
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