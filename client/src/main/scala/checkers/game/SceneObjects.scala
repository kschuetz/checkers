package checkers.game

sealed trait SceneObject {
  def isAnimating: Boolean
}

sealed trait PieceType

object PieceType {
  case object Man extends PieceType
  case object King extends PieceType
}

case class Piece(id: Int, pieceType: PieceType, color: Color, row: Int, col: Int, clickable: Boolean, highlighted: Boolean)

