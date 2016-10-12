package checkers

import language.experimental.macros
import scala.reflect.macros.blackbox

package object masks {
  private def squareMaskFromSeq(squares: Seq[Int]): Int = {
    var result = 0
    squares.foreach { s => result |= (1 << s) }
    result
  }

  private def squareMask(squares: Int*): Int = squareMaskFromSeq(squares)

//  private val l3 = squareMask(1, 2, 3, 9, 10, 11, 17, 18, 19, 25, 26, 27)
//  private val l5 = squareMask(4, 5, 6, 12, 13, 14, 20, 21, 22)
//  private val r3 = squareMask(28, 29, 30, 20, 21, 22, 12, 13, 14, 4, 5, 6)
//  private val r5 = squareMask(25, 26, 27, 17, 18, 19, 9, 10, 11)
//  private val top = squareMaskFromSeq(20 to 31)
//  private val bottom = squareMaskFromSeq(0 to 11)
//  private val trappedLight = squareMaskFromSeq(0 to 4)
//  private val trappedDark = squareMaskFromSeq(27 to 31)
//  private val second = squareMaskFromSeq(4 to 7)
//  private val seventh = squareMaskFromSeq(24 to 27)
//  private val edges = squareMask(24, 20, 16, 12, 8, 4, 0, 7, 11, 15, 19, 23, 27, 31)
  private val outer = squareMask(0, 1, 2, 3, 7, 8, 15, 16, 23, 24, 28, 29, 30, 31)
  private val inner = ~outer
  private val nw3 = squareMask(4, 5, 6, 12, 13, 14, 20, 21, 22, 28, 29, 30)
  private val nw4 = squareMask(8, 9, 10, 11, 16, 17, 18, 19, 24, 25, 26, 27)

  private val ne4 = squareMask(4, 5, 6, 7, 12, 13, 14, 15, 20, 21, 22, 23, 28, 29, 30, 31)
  private val ne5 = squareMask(9, 10, 11, 17, 18, 19, 25, 26, 27)

  private val sw4 = squareMask(0, 1, 2, 3, 8, 9, 10, 11, 16, 17, 18, 19, 24, 25, 26, 27)
  private val sw5 = squareMask(4, 5, 6, 12, 13, 14, 20, 21, 22)

  private val se3 = squareMask(1, 2, 3, 9, 10, 11, 17, 18, 19, 25, 26, 27)
  private val se4 = squareMask(4, 5, 6, 7, 12, 13, 14, 15, 20, 21, 22, 23)

  private val leftEdge = squareMask(0, 4, 8, 12, 16, 20, 24, 28)
  private val rightEdge = squareMask(3, 7, 11, 15, 19, 23, 27, 31)

  private val notLeftEdge = ~leftEdge
  private val notRightEdge = ~rightEdge

  private val lightBack = squareMaskFromSeq(28 to 31)
  private val lightSecond = squareMaskFromSeq(24 to 27)
  private val lightThird = squareMaskFromSeq(20 to 23)
  private val lightFourth = squareMaskFromSeq(16 to 19)

  private val darkBack = squareMaskFromSeq(0 to 3)
  private val darkSecond = squareMaskFromSeq(4 to 7)
  private val darkThird = squareMaskFromSeq(8 to 11)
  private val darkFourth = squareMaskFromSeq(12 to 15)

  private val crownLight = darkBack
  private val crownDark = lightBack

  def OUTER: Int = macro outerImpl
  def INNER: Int = macro innerImpl

  def CROWNLIGHT: Int = macro crownLightImpl
  def CROWNDARK: Int = macro crownDarkImpl

  def NW3: Int = macro nw3Impl
  def NW4: Int = macro nw4Impl
  def NE4: Int = macro ne4Impl
  def NE5: Int = macro ne5Impl
  def SW4: Int = macro sw4Impl
  def SW5: Int = macro sw5Impl
  def SE3: Int = macro se3Impl
  def SE4: Int = macro se4Impl
  def NLE: Int = macro nleImpl
  def NRE: Int = macro nreImpl

  def LIGHTBACK: Int = macro lightBackImpl
  def LIGHTSECOND: Int = macro lightSecondImpl
  def LIGHTTHIRD: Int = macro lightThirdImpl
  def LIGHTFOURTH: Int = macro lightFourthImpl

  def DARKBACK: Int = macro darkBackImpl
  def DARKSECOND: Int = macro darkSecondImpl
  def DARKTHIRD: Int = macro darkThirdImpl
  def DARKFOURTH: Int = macro darkFourthImpl

  /**
    * Notice: Some SHIFT macros will evaluate the argument twice.
    */
  def SHIFTN(board: Int): Int = macro shiftNImpl
  def SHIFTE(board: Int): Int = macro shiftEImpl
  def SHIFTS(board: Int): Int = macro shiftSImpl
  def SHIFTW(board: Int): Int = macro shiftWImpl

  def SHIFTNW(board: Int): Int = macro shiftNWImpl
  def SHIFTNE(board: Int): Int = macro shiftNEImpl
  def SHIFTSW(board: Int): Int = macro shiftSWImpl
  def SHIFTSE(board: Int): Int = macro shiftSEImpl

  def SHIFTNW2(board: Int): Int = macro shiftNW2Impl
  def SHIFTNE2(board: Int): Int = macro shiftNE2Impl
  def SHIFTSW2(board: Int): Int = macro shiftSW2Impl
  def SHIFTSE2(board: Int): Int = macro shiftSE2Impl

  def outerImpl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(outer)))
  }

  def innerImpl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(inner)))
  }

  def crownLightImpl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(crownLight)))
  }

  def crownDarkImpl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(crownDark)))
  }

  def nw3Impl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(nw3)))
  }

  def nw4Impl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(nw4)))
  }

  def ne4Impl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(ne4)))
  }

  def ne5Impl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(ne5)))
  }

  def sw4Impl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(sw4)))
  }

  def sw5Impl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(sw5)))
  }

  def se3Impl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(se3)))
  }

  def se4Impl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(se4)))
  }

  def nleImpl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(notLeftEdge)))
  }

  def nreImpl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(notRightEdge)))
  }

  def lightBackImpl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(lightBack)))
  }

  def lightSecondImpl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(lightSecond)))
  }

  def lightThirdImpl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(lightThird)))
  }

  def lightFourthImpl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(lightFourth)))
  }

  def darkBackImpl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(darkBack)))
  }

  def darkSecondImpl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(darkSecond)))
  }

  def darkThirdImpl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(darkThird)))
  }

  def darkFourthImpl(c: blackbox.Context): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](Literal(Constant(darkFourth)))
  }

  def shiftNImpl(c: blackbox.Context)(board: c.Expr[Int]): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](q"($board << 8)")
  }

  def shiftSImpl(c: blackbox.Context)(board: c.Expr[Int]): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](q"($board >>> 8)")
  }

  def shiftEImpl(c: blackbox.Context)(board: c.Expr[Int]): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](q"($board << 1) & NLE")
  }

  def shiftWImpl(c: blackbox.Context)(board: c.Expr[Int]): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](q"($board >>> 1) & NRE")
  }

  def shiftNWImpl(c: blackbox.Context)(board: c.Expr[Int]): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](q"(($board << 3) & NW3) | (($board << 4) & NW4)")
  }

  def shiftNEImpl(c: blackbox.Context)(board: c.Expr[Int]): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](q"(($board << 4) & NE4) | (($board << 5) & NE5)")
  }

  def shiftSWImpl(c: blackbox.Context)(board: c.Expr[Int]): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](q"(($board >> 4) & SW4) | (($board >> 5) & SW5)")
  }

  def shiftSEImpl(c: blackbox.Context)(board: c.Expr[Int]): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](q"(($board >> 3) & SE3) | (($board >> 4) & SE4)")
  }

  def shiftNW2Impl(c: blackbox.Context)(board: c.Expr[Int]): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](q"($board << 7) & NRE")
  }

  def shiftNE2Impl(c: blackbox.Context)(board: c.Expr[Int]): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](q"($board << 9) & NLE")
  }

  def shiftSW2Impl(c: blackbox.Context)(board: c.Expr[Int]): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](q"($board >>> 9) & NRE")
  }

  def shiftSE2Impl(c: blackbox.Context)(board: c.Expr[Int]): c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](q"($board >>> 7) & NLE")
  }


}
