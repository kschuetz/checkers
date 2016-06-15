package checkers.core

import checkers.consts._

//sealed trait PieceType
//
//object PieceType {
//
//  case object Man extends PieceType
//
//  case object King extends PieceType
//
//}

//sealed trait Occupant {
//  def getPiece: Option[Piece]
//  def isEmpty: Boolean
//  def isMan: Boolean
//  def isKing: Boolean
//  def code: Int
//  def crowned: Occupant
//  def foreach[A](f: Piece => A): Unit
//}
//
//case object Empty extends Occupant {
//  val getPiece = None
//  val isEmpty = true
//  val isMan = false
//  val isKing = false
//  val code: Int = 0
//  val crowned = Empty
//  def foreach[A](f: Piece => A): Unit = { }
//}
//
//sealed trait Piece extends Occupant {
//  def color: Color
//  def pieceType: PieceType
//  val getPiece = Some(this)
//  val isEmpty = false
//  def foreach[A](f: Piece => A): Unit = f(this)
//}
//
//case object LightMan extends Piece {
//  val color = Light
//  val pieceType = Man
//  val code: Int = 4
//  val isMan = true
//  val isKing = false
//  val crowned = LightKing
//}
//
//case object DarkMan extends Piece {
//  val color = Dark
//  val pieceType = Man
//  val code: Int = 5
//  val isMan = true
//  val isKing = false
//  val crowned = DarkKing
//}
//
//case object LightKing extends Piece {
//  val color = Light
//  val pieceType = King
//  val code: Int = 6
//  val isMan = false
//  val isKing = true
//  val crowned = LightKing
//}
//
//case object DarkKing extends Piece {
//  val color = Dark
//  val pieceType = King
//  val code: Int = 7
//  val isMan = false
//  val isKing = true
//  val crowned = DarkKing
//}

object Occupant {
  def color(occupant: Occupant): Color = occupant & 1
  def pieceType(occupant: Occupant): PieceType = occupant & 2
  def code(occupant: Occupant): Int = occupant
  def isMan(occupant: Occupant): Boolean = (occupant & 6) == 4
  def isKing(occupant: Occupant): Boolean = (occupant & 6) == 6
  def isEmpty(occupant: Occupant): Boolean = occupant < 4
  def isPiece(occupant: Occupant): Boolean = occupant >= 4
  def crowned(occupant: Occupant): Occupant = if(isMan(occupant)) occupant & 2 else occupant
}

