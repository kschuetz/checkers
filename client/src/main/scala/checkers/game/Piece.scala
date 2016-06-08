package checkers.game

sealed trait PieceType

object PieceType {

  case object Man extends PieceType

  case object King extends PieceType

}

sealed trait Occupant {
  def getPiece: Option[Piece]
  def isEmpty: Boolean
  def code: Int
}

case object Empty extends Occupant {
  val getPiece = None
  val isEmpty = true
  val code: Int = 0
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
}

case object DarkMan extends Piece {
  val color = Dark
  val pieceType = PieceType.Man
  val code: Int = 5
}

case object LightKing extends Piece {
  val color = Light
  val pieceType = PieceType.King
  val code: Int = 6
}

case object DarkKing extends Piece {
  val color = Dark
  val pieceType = PieceType.King
  val code: Int = 7
}