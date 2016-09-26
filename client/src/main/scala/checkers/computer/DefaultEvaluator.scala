package checkers.computer

import checkers.consts._
import checkers.core.{BoardStateRead, RulesSettings}
import checkers.masks._


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
  private val TrappedKingPenalty = 50
  private val TurnAdvantageBonus = 3
  private val RunawayBaseBonus = 50
  private val BackRankBonus = 6

  def evaluate(turnToPlay: Color, board: BoardStateRead, testProbe: AnyRef = null): Int = {
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
    val emptyNW2 = SHIFTSE(emptyNW)
    val emptyNE2 = SHIFTSE(emptyNE)
    val emptySW2 = SHIFTSE(emptySW)
    val emptySE2 = SHIFTSE(emptySE)
    val kingNW = SHIFTSE(k)
    val kingNE = SHIFTSW(k)
    val kingSW = SHIFTNE(k)
    val kingSE = SHIFTNW(k)

    val darkKingNW = darkNW & kingNW
    val darkKingNE = darkNE & kingNE
    val lightKingSW = lightSW & kingSW
    val lightKingSE = lightSE & kingSE

    val potentialAttacks = INNER & notOccupied

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

    val darkKingCanJump = dp & k & ((emptyNE2 & lightNE) |
      (emptySE2 & lightSE) |
      (emptySW2 & lightSW) |
      (emptyNW2 & lightNW))

    val lightKingCanJump = lp & k & ((emptyNE2 & darkNE) |
      (emptySE2 & darkSE) |
      (emptySW2 & darkSW) |
      (emptyNW2 & darkNW))

    val safeForDark = notOccupied & (~lightAttacks)
    val safeForLight = notOccupied & (~darkAttacks)

    val darkEscapeMove = SHIFTNW(safeForDark) | SHIFTNE(safeForDark) | SHIFTSW(safeForDark) | SHIFTSE(safeForDark)
    val lightEscapeMove = SHIFTNW(safeForLight) | SHIFTNE(safeForLight) | SHIFTSW(safeForLight) | SHIFTSE(safeForLight)

    val darkCanEscape = darkEscapeMove | darkKingCanJump
    val lightCanEscape = lightEscapeMove | lightKingCanJump

    var darkMen = 0
    var darkKings = 0
    var lightMen = 0
    var lightKings = 0
    var darkMaterial = 0
    var lightMaterial = 0
    var darkTrappedKings = 0
    var lightTrappedKings = 0

    var i = 0
    while (i < 32) {
      if (((dp >>> i) & 1) != 0) {
        if (((k >>> i) & 1) != 0) {
          darkKings += 1
          darkMaterial += King
          if (((darkCanEscape >>> i) & 1) == 0) {
            darkTrappedKings += 1
          }
        } else {
          darkMen += 1
          darkMaterial += Man
        }
      } else if (((lp >>> i) & 1) != 0) {
        if (((k >>> i) & 1) != 0) {
          lightKings += 1
          lightMaterial += King
          if (((lightCanEscape >>> i) & 1) == 0) {
            lightTrappedKings += 1
          }
        } else {
          lightMen += 1
          lightMaterial += Man
        }
      }
      i += 1
    }

    var result = TurnAdvantageBonus
    if (turnToPlay == DARK) {
      result += darkMaterial - lightMaterial
    } else {
      result += lightMaterial - darkMaterial
    }

    if (probe != null) {
      val darkTrappedKingLocations = (~darkEscapeMove) & k & dp
      val lightTrappedKingLocations = (~lightEscapeMove) & k & lp

      probe.darkManCount = darkMen
      probe.darkKingCount = darkKings
      probe.darkTrappedKingCount = darkTrappedKings
      probe.lightManCount = lightMen
      probe.lightKingCount = lightKings
      probe.lightTrappedKingCount = lightTrappedKings
      probe.potentialAttackMask = potentialAttacks
      probe.darkAttackMask = darkAttacks
      probe.lightAttackMask = lightAttacks
      probe.darkSafeMask = safeForDark
      probe.lightSafeMask = safeForLight
      probe.darkCanEscapeMask = darkEscapeMove
      probe.lightCanEscapeMask = lightEscapeMove
      probe.darkTrappedKingMask = darkTrappedKingLocations
      probe.lightTrappedKingMask = lightTrappedKingLocations
    }

    if (rulesSettings.giveaway) -result else result
  }

}

class DefaultEvaluatorTestProbe {
  var darkManCount: Int = 0
  var darkKingCount: Int = 0
  var darkTrappedKingCount: Int = 0
  var lightManCount: Int = 0
  var lightKingCount: Int = 0
  var lightTrappedKingCount: Int = 0
  var potentialAttackMask: Int = 0
  var darkAttackMask: Int = 0
  var lightAttackMask: Int = 0
  var darkSafeMask: Int = 0
  var lightSafeMask: Int = 0
  var darkCanEscapeMask: Int = 0
  var lightCanEscapeMask: Int = 0
  var darkTrappedKingMask: Int = 0
  var lightTrappedKingMask: Int = 0

}