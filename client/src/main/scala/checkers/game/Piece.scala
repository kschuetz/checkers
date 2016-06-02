package checkers.game

sealed trait PieceType

object PieceType {

  case object Man extends PieceType

  case object King extends PieceType

}

sealed trait Occupant {
  def getPiece: Option[Piece]
  def isEmpty: Boolean
}

case object Empty extends Occupant {
  val getPiece = None
  val isEmpty = true
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
}

case object DarkMan extends Piece {
  val color = Dark
  val pieceType = PieceType.Man
}

case object LightKing extends Piece {
  val color = Light
  val pieceType = PieceType.King
}

case object DarkKing extends Piece {
  val color = Dark
  val pieceType = PieceType.King
}