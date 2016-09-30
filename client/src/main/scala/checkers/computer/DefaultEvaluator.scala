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
    val occupied = lp | dp
    val empty = ~(lp | dp)
    val nonDark = empty | lp
    val nonLight = empty | dp

    val darkN = SHIFTS(dp)
    val darkE = SHIFTW(dp)
    val darkS = SHIFTN(dp)
    val darkW = SHIFTE(dp)
    val darkNW = SHIFTSE(dp)
    val darkNE = SHIFTSW(dp)
    val darkSW = SHIFTNE(dp)
    val darkSE = SHIFTNW(dp)
    val darkNW2 = SHIFTSE2(dp)
    val darkNE2 = SHIFTSW2(dp)
    val darkSW2 = SHIFTNE2(dp)
    val darkSE2 = SHIFTNW2(dp)

    val lightN = SHIFTS(lp)
    val lightE = SHIFTW(lp)
    val lightS = SHIFTN(lp)
    val lightW = SHIFTE(lp)
    val lightNW = SHIFTSE(lp)
    val lightNE = SHIFTSW(lp)
    val lightSW = SHIFTNE(lp)
    val lightSE = SHIFTNW(lp)
    val lightNW2 = SHIFTSE2(lp)
    val lightNE2 = SHIFTSW2(lp)
    val lightSW2 = SHIFTNE2(lp)
    val lightSE2 = SHIFTNW2(lp)

    val emptyN = SHIFTS(empty)
    val emptyE = SHIFTW(empty)
    val emptyS = SHIFTN(empty)
    val emptyW = SHIFTE(empty)
    val emptyNW = SHIFTSE(empty)
    val emptyNE = SHIFTSW(empty)
    val emptySW = SHIFTNE(empty)
    val emptySE = SHIFTNW(empty)
    val emptyNW2 = SHIFTSE2(empty)
    val emptyNE2 = SHIFTSW2(empty)
    val emptySW2 = SHIFTNE2(empty)
    val emptySE2 = SHIFTNW2(empty)

    val kingN = SHIFTS(k)
    val kingE = SHIFTW(k)
    val kingS = SHIFTN(k)
    val kingW = SHIFTE(k)
    val kingNW = SHIFTSE(k)
    val kingNE = SHIFTSW(k)
    val kingSW = SHIFTNE(k)
    val kingSE = SHIFTNW(k)
    val kingNW2 = SHIFTSE2(k)
    val kingNE2 = SHIFTSW2(k)
    val kingSW2 = SHIFTNE2(k)
    val kingSE2 = SHIFTNW2(k)

    // closed:  occupied or out of bounds
    val closedNW = ~emptyNW
    val closedNE = ~emptyNE
    val closedSE = ~emptySE
    val closedSW = ~emptySW

    // vulnerable:  the square to a given direction is under attack (if empty)
    val darkVulnerableNW = lightNW2 | (lightN & emptyW) | (emptyN & lightW & kingW)
    val darkVulnerableNE = lightNE2 | (lightN & emptyE) | (emptyN & lightE & kingE)
    val darkVulnerableSW = (lightSW2 & kingSW2) | (lightS & kingS & emptyW) | (emptyS & lightW)
    val darkVulnerableSE = (lightSE2 & kingSE2) | (lightS & kingS & emptyE) | (emptyS & lightE)

    val lightVulnerableNW = (darkNW2 & kingNW2) | (darkN & kingN & emptyW) | (emptyN & darkW)
    val lightVulnerableNE = (darkNE2 & kingNE2) | (darkN & kingN & emptyE) | (emptyN & darkE)
    val lightVulnerableSW = darkSW2 | (darkS & emptyW) | (emptyS & darkW & kingW)
    val lightVulnerableSE = darkSE2 | (darkS & emptyE) | (emptyS & darkE & kingE)

    // trapped:  can't move due to being blocked or under attack
    val darkTrappedNW = closedNW | darkVulnerableNW
    val darkTrappedNE = closedNE | darkVulnerableNE
    val darkTrappedSW = closedSW | darkVulnerableSW
    val darkTrappedSE = closedSE | darkVulnerableSE

    val lightTrappedNW = closedNW | lightVulnerableNW
    val lightTrappedNE = closedNE | lightVulnerableNE
    val lightTrappedSW = closedSW | lightVulnerableSW
    val lightTrappedSE = closedSE | lightVulnerableSE

    val darkTrappedForward = darkTrappedNW & darkTrappedNE
    val darkTrappedRear = darkTrappedSW & darkTrappedSE
    val lightTrappedForward = lightTrappedSW & lightTrappedSE
    val lightTrappedRear = lightTrappedNW & lightTrappedNE

    val darkEscapeMove = ~(darkTrappedForward & darkTrappedRear)
    val lightEscapeMove = ~(lightTrappedRear & lightTrappedForward)

    val potentialAttacks = INNER & empty

    val darkAttacksNE = nonDark & lightVulnerableNE
    val darkAttacksNW = nonDark & lightVulnerableNW
    val darkAttacksSW = nonDark & lightVulnerableSW
    val darkAttacksSE = nonDark & lightVulnerableSE

    val lightAttacksNE = nonLight & darkVulnerableNE
    val lightAttacksNW = nonLight & darkVulnerableNW
    val lightAttacksSW = nonLight & darkVulnerableSW
    val lightAttacksSE = nonLight & darkVulnerableSE
    
    val darkAttacks = potentialAttacks & (
      SHIFTNE(darkAttacksNE) |
        SHIFTNW(darkAttacksNW) |
        SHIFTSE(darkAttacksSE) |
        SHIFTSW(darkAttacksSW))

    val lightAttacks = potentialAttacks & (
      SHIFTNE(lightAttacksNE) |
        SHIFTNW(lightAttacksNW) |
        SHIFTSE(lightAttacksSE) |
        SHIFTSW(lightAttacksSW))

    val darkKingCanJump = dp & k & ((emptyNE2 & lightNE) |
      (emptySE2 & lightSE) |
      (emptySW2 & lightSW) |
      (emptyNW2 & lightNW))

    val lightKingCanJump = lp & k & ((emptyNE2 & darkNE) |
      (emptySE2 & darkSE) |
      (emptySW2 & darkSW) |
      (emptyNW2 & darkNW))

    val safeForDark = empty & (~lightAttacks)
    val safeForLight = empty & (~darkAttacks)

    val darkKingCanEscape = darkEscapeMove | darkKingCanJump
    val lightKingCanEscape = lightEscapeMove | lightKingCanJump

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
          if (((darkKingCanEscape >>> i) & 1) == 0) {
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
          if (((lightKingCanEscape >>> i) & 1) == 0) {
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
      val darkTrappedKingLocations = (~darkKingCanEscape) & k & dp
      val lightTrappedKingLocations = (~lightKingCanEscape) & k & lp

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
      probe.closedNWMask = closedNW
      probe.closedNEMask = closedNE
      probe.closedSWMask = closedSW
      probe.closedSEMask = closedSE
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
  var closedNWMask: Int = 0
  var closedNEMask: Int = 0
  var closedSWMask: Int = 0
  var closedSEMask: Int = 0

}