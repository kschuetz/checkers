package checkers

import language.experimental.macros
import scala.reflect.macros.blackbox

package object consts {
  type Side = Int

  def DARK: Side = macro darkImpl
  def LIGHT: Side = macro lightImpl

  type PieceType = Int

  def MAN: PieceType = macro manImpl
  def KING: PieceType = macro kingImpl

  def MAKEMAN(side: Side): Occupant = macro makeManImpl
  def MAKEKING(side: Side): Occupant = macro makeKingImpl

  type Occupant = Int

  def EMPTY: Occupant = macro emptyImpl
  def LIGHTMAN: Occupant  = macro lightManImpl
  def DARKMAN: Occupant  = macro darkManImpl
  def LIGHTKING: Occupant  = macro lightKingImpl
  def DARKKING: Occupant  = macro darkKingImpl

  type EncodedOutcome = Int
  type OutcomeType = Int
  type OutcomeValue = Int

  def WIN: OutcomeType = macro winImpl
  def SCORE: OutcomeType = macro scoreImpl
  def DRAW: OutcomeType = macro drawImpl
  def LOSS: OutcomeType = macro lossImpl

  def ENCODEOUTCOME(outcomeType: OutcomeType, outcomeValue: OutcomeValue): EncodedOutcome = macro encodeOutcomeImpl

  def OUTCOMETYPE(encodedOutcome: EncodedOutcome): OutcomeType = macro outcomeTypeImpl

  def OUTCOMEVALUE(encodedOutcome: EncodedOutcome): OutcomeValue = macro outcomeValueImpl


  val MoveListFrameSize = 12

  def MOVELISTFRAMESIZE: Int = macro moveListFrameSizeImpl

  def SIDE(occupant: Occupant): Side = macro sideImpl
  def PIECETYPE(occupant: Occupant): PieceType = macro pieceTypeImpl
  def ISPIECE(occupant: Occupant): Boolean = macro isPieceImpl
  def ISEMPTY(occupant: Occupant): Boolean = macro isEmptyImpl
  def OPPONENT(side: Side): Side = macro opponentImpl

  def PROPOSEDDRAW: Int = macro proposedDrawImpl
  def PIECEADVANCED: Int = macro pieceAdvancedImpl
  def PIECECROWNED: Int = macro pieceCrownedImpl
  def PIECECAPTURED: Int = macro pieceCapturedImpl

  def darkImpl(c: blackbox.Context): c.Expr[Side] = c.universe.reify(0)
  def lightImpl(c: blackbox.Context): c.Expr[Side] = c.universe.reify(1)

  def manImpl(c: blackbox.Context): c.Expr[PieceType] = c.universe.reify(0)
  def kingImpl(c: blackbox.Context): c.Expr[PieceType] = c.universe.reify(1)

  def emptyImpl(c: blackbox.Context): c.Expr[Occupant] = c.universe.reify(0)
  def darkManImpl(c: blackbox.Context): c.Expr[Occupant] = c.universe.reify(1)
  def lightManImpl(c: blackbox.Context): c.Expr[Occupant] = c.universe.reify(2)
  def darkKingImpl(c: blackbox.Context): c.Expr[Occupant] = c.universe.reify(5)
  def lightKingImpl(c: blackbox.Context): c.Expr[Occupant] = c.universe.reify(6)

  def winImpl(c: blackbox.Context): c.Expr[Int] = c.universe.reify(3)
  def scoreImpl(c: blackbox.Context): c.Expr[Int] = c.universe.reify(2)
  def drawImpl(c: blackbox.Context): c.Expr[Int] = c.universe.reify(1)
  def lossImpl(c: blackbox.Context): c.Expr[Int] = c.universe.reify(0)

  def proposedDrawImpl(c: blackbox.Context): c.Expr[Int] = c.universe.reify(1)
  def pieceAdvancedImpl(c: blackbox.Context): c.Expr[Int] = c.universe.reify(2)
  def pieceCrownedImpl(c: blackbox.Context): c.Expr[Int] = c.universe.reify(4)
  def pieceCapturedImpl(c: blackbox.Context): c.Expr[Int] = c.universe.reify(8)

  def moveListFrameSizeImpl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(MoveListFrameSize)))
  }

  def sideImpl(c: blackbox.Context)(occupant: c.Expr[Occupant]): c.Expr[Side] = {
    import c.universe._
    c.Expr[Side](q"($occupant >> 1) & 1")
  }

  def opponentImpl(c: blackbox.Context)(side: c.Expr[Side]): c.Expr[Side] = {
    import c.universe._
    c.Expr[Side](q"(~$side) & 1")
  }

  def pieceTypeImpl(c: blackbox.Context)(occupant: c.Expr[Occupant]): c.Expr[PieceType] = {
    import c.universe._
    c.Expr[PieceType](q"($occupant >> 2) & 1")
  }

  def isEmptyImpl(c: blackbox.Context)(occupant: c.Expr[Occupant]): c.Expr[Boolean] = {
    import c.universe._
    c.Expr[Boolean](q"$occupant == 0")
  }

  def isPieceImpl(c: blackbox.Context)(occupant: c.Expr[Occupant]): c.Expr[Boolean] = {
    import c.universe._
    c.Expr[Boolean](q"$occupant > 0")
  }

  def makeManImpl(c: blackbox.Context)(side: c.Expr[Side]): c.Expr[Occupant] = {
    import c.universe._
    c.Expr[Occupant](q"if($side == LIGHT) LIGHTMAN else DARKMAN")
  }

  def makeKingImpl(c: blackbox.Context)(side: c.Expr[Side]): c.Expr[Occupant] = {
    import c.universe._
    c.Expr[Occupant](q"if($side == LIGHT) LIGHTKING else DARKKING")
  }

  def encodeOutcomeImpl(c: blackbox.Context)(outcomeType: c.Expr[OutcomeType], outcomeValue: c.Expr[OutcomeValue]): c.Expr[EncodedOutcome] = {
    import c.universe._
    c.Expr[EncodedOutcome](q"($outcomeValue << 2) | $outcomeType")
  }

  def outcomeTypeImpl(c: blackbox.Context)(encodedOutcome: c.Expr[EncodedOutcome]): c.Expr[OutcomeType] = {
    import c.universe._
    c.Expr[OutcomeType](q"$encodedOutcome & 3")
  }

  def outcomeValueImpl(c: blackbox.Context)(encodedOutcome: c.Expr[EncodedOutcome]): c.Expr[OutcomeType] = {
    import c.universe._
    c.Expr[OutcomeType](q"$encodedOutcome >> 2")
  }

}
