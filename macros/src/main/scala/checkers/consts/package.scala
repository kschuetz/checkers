package checkers

import language.experimental.macros
import scala.reflect.macros.blackbox

package object consts {
  type Color = Int

  def Dark: Color = macro darkImpl
  def Light: Color = macro lightImpl

  type PieceType = Int

  def Man: PieceType = macro manImpl
  def King: PieceType = macro kingImpl

  type Occupant = Int

  def Empty: Occupant = macro emptyImpl
  def LightMan: Occupant  = macro lightManImpl
  def DarkMan: Occupant  = macro darkManImpl
  def LightKing: Occupant  = macro lightKingImpl
  def DarkKing: Occupant  = macro darkKingImpl

  def darkImpl(c: blackbox.Context): c.Expr[Color] = c.universe.reify(0)
  def lightImpl(c: blackbox.Context): c.Expr[Color] = c.universe.reify(1)

  def manImpl(c: blackbox.Context): c.Expr[PieceType] = c.universe.reify(0)
  def kingImpl(c: blackbox.Context): c.Expr[PieceType] = c.universe.reify(2)

  def emptyImpl(c: blackbox.Context): c.Expr[Occupant] = c.universe.reify(0)
  def lightManImpl(c: blackbox.Context): c.Expr[Occupant] = c.universe.reify(4)
  def darkManImpl(c: blackbox.Context): c.Expr[Occupant] = c.universe.reify(5)
  def lightKingImpl(c: blackbox.Context): c.Expr[Occupant] = c.universe.reify(6)
  def darkKingImpl(c: blackbox.Context): c.Expr[Occupant] = c.universe.reify(7)

  def colorOf2(occupant: Occupant): Color = macro colorOfImpl

  def colorOfImpl(c: blackbox.Context)(occupant: c.Expr[Occupant]): c.Expr[Color] = {
    import c.universe._
    c.Expr[Color](q"$occupant & 1")
  }
}
