package checkers.computer

import checkers.consts._
import checkers.masks._
import checkers.core.{BoardStateRead, RulesSettings}

import scala.scalajs.js.typedarray.Int32Array


class DefaultEvaluator(rulesSettings: RulesSettings) extends Evaluator {


  //           28  29  30  31
  //         24  25  26  27
  //           20  21  22  23
  //         16  17  18  19
  //           12  13  14  15
  //         08  09  10  11
  //           04  05  06  07
  //         00  01  02  03


  private val Man = 100
  private val King = 150

  private val DogHoleBonus = 10
  private val TurnAdvantageBonus = 3
  private val RunawayBaseBonus = 50
  private val BackRankBonus = 6

  private val innerSquares = ~checkers.core.masks.outer

  def evaluate(color: Color, turnToPlay: Color, board: BoardStateRead, testProbe: AnyRef = null): Int = {
    val probe = if (testProbe == null) null else testProbe.asInstanceOf[DefaultEvaluatorTestProbe]

    val k = board.kings
    val lp = board.lightPieces
    val dp = board.darkPieces
    val notOccupied = ~(lp | dp)

    val darkNW = SHIFTSE(dp)
    val darkNE = SHIFTSW(dp)
    val darkSW = SHIFTNE(dp)
    val darkSE = SHIFTNW(dp)
    val lightNW = SHIFTSE(lp)
    val lightNE = SHIFTSW(lp)
    val lightSW = SHIFTNE(lp)
    val lightSE = SHIFTNW(lp)
    val emptyNW = SHIFTSE(notOccupied)
    val emptyNE = SHIFTSW(notOccupied)
    val emptySW = SHIFTNE(notOccupied)
    val emptySE = SHIFTNW(notOccupied)
    val kingNW = SHIFTSE(k)
    val kingNE = SHIFTSW(k)
    val kingSW = SHIFTNE(k)
    val kingSE = SHIFTNW(k)

    val darkKingNW = darkNW & kingNW
    val darkKingNE = darkNE & kingNE
    val lightKingSW = lightSW & kingSW
    val lightKingSE = lightSE & kingSE

    val potentialAttacks = innerSquares & notOccupied

    val darkAttacks = potentialAttacks &
      ((darkSW & (emptyNE | lightNE)) |
        (darkSE & (emptyNW | lightNW)) |
        (darkKingNW & (emptySE | lightSE)) |
        (darkKingNE & (emptySW | lightSW)))

    val lightAttacks = potentialAttacks &
      ((lightNW & (emptySE | darkSE)) |
        (lightNE & (emptySW | darkSW)) |
        (lightKingSW & (emptyNE | darkNE)) |
        (lightKingSE & (emptyNW | darkNW)))

    val safeForDark = notOccupied & (~lightAttacks)
    val safeForLight = notOccupied & (~darkAttacks)

    var darkMen = 0
    var darkKings = 0
    var lightMen = 0
    var lightKings = 0
    var darkMaterial = 0
    var lightMaterial = 0

    var i = 0
    while (i < 32) {
      if (((dp >>> i) & 1) != 0) {
        if (((k >>> i) & 1) != 0) {
          darkKings += 1
          darkMaterial += King
        } else {
          darkMen += 1
          darkMaterial += Man
        }
      } else if (((lp >>> i) & 1) != 0) {
        if (((k >>> i) & 1) != 0) {
          lightKings += 1
          lightMaterial += King
        } else {
          lightMen += 1
          lightMaterial += Man
        }
      }
      i += 1
    }

    var result = 0
    if (color == DARK) {
      result += darkMaterial - lightMaterial
    } else {
      result += lightMaterial - darkMaterial
    }

    if (turnToPlay == color) result += TurnAdvantageBonus

    if (probe != null) {
      probe.darkMen = darkMen
      probe.darkKings = darkKings
      probe.lightMen = lightMen
      probe.lightKings = lightKings
      probe.potentialAttacks = potentialAttacks
      probe.darkAttacks = darkAttacks
      probe.lightAttacks = lightAttacks
      probe.safeForDark = safeForDark
      probe.safeForLight = safeForLight
    }

    if (rulesSettings.giveaway) -result else result
  }

}

class DefaultEvaluatorTestProbe {
  var darkMen: Int = 0
  var darkKings: Int = 0
  var lightMen: Int = 0
  var lightKings: Int = 0
  var potentialAttacks: Int = 0
  var darkAttacks: Int = 0
  var lightAttacks: Int = 0
  var safeForDark: Int = 0
  var safeForLight: Int = 0
}