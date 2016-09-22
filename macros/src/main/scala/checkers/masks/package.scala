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

  private val nw3 = squareMask(4, 5, 6, 12, 13, 14, 20, 21, 22, 28, 29, 30)
  private val nw4 = squareMask(8, 9, 10, 11, 16, 17, 18, 19, 24, 25, 26, 27)

  private val ne4 = squareMask(4, 5, 6, 7, 12, 13, 14, 15, 20, 21, 22, 23, 28, 29, 30, 31)
  private val ne5 = squareMask(9, 10, 11, 17, 18, 19, 25, 26, 27)

  private val sw4 = squareMask(0, 1, 2, 3, 8, 9, 10, 11, 16, 17, 18, 19, 24, 25, 26, 27)
  private val sw5 = squareMask(4, 5, 6, 12, 13, 14, 20, 21, 22)

  private val se3 = squareMask(1, 2, 3, 9, 10, 11, 17, 18, 19, 25, 26, 27)
  private val se4 = squareMask(4, 5, 6, 7, 12, 13, 14, 15, 20, 21, 22, 23)

  def NW3: Int = macro nw3Impl
  def NW4: Int = macro nw4Impl
  def NE4: Int = macro ne4Impl
  def NE5: Int = macro ne5Impl
  def SW4: Int = macro sw4Impl
  def SW5: Int = macro sw5Impl
  def SE3: Int = macro se3Impl
  def SE4: Int = macro se4Impl

  def SHIFTNW(board: Int): Int = macro shiftNWImpl
  def SHIFTNE(board: Int): Int = macro shiftNEImpl
  def SHIFTSW(board: Int): Int = macro shiftSWImpl
  def SHIFTSE(board: Int): Int = macro shiftSEImpl


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
  //  @inline def shiftNW(board: Int): Int = ((board << 3) & checkers.masks.NW3) | ((board << 4) & checkers.masks.NW4)
  //
  //  @inline def shiftNE(board: Int): Int = ((board << 4) & checkers.masks.NE4) | ((board << 5) & checkers.masks.NE5)
  //
  //  @inline def shiftSW(board: Int): Int = ((board >> 4) & checkers.masks.SW4) | ((board >> 5) & checkers.masks.SW5)
  //
  //  @inline def shiftSE(board: Int): Int = ((board >> 3) & checkers.masks.SE3) | ((board >> 4) & checkers.masks.SE4)

}