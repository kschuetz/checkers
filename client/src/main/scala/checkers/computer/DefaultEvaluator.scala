package checkers.computer

import checkers.consts._
import checkers.core.{BoardStateRead, RulesSettings, masks}

import scala.scalajs.js.typedarray.Int32Array


class DefaultEvaluator(rulesSettings: RulesSettings) extends Evaluator {
  
  import masks._

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

  private val innerSquares = ~masks.outer

  def evaluate(color: Color, turnToPlay: Color, board: BoardStateRead, testProbe: AnyRef = null): Int = {
    val probe = if(testProbe == null) null else testProbe.asInstanceOf[DefaultEvaluatorTestProbe]

    val k = board.kings
    val lp = board.lightPieces
    val dp = board.darkPieces
    val notOccupied = ~(lp | dp)
    
    val darkNW = shiftSE(dp)
    val darkNE = shiftSW(dp)
    val darkSW = shiftNE(dp)
    val darkSE = shiftNW(dp)
    val lightNW = shiftSE(lp)
    val lightNE = shiftSW(lp)
    val lightSW = shiftNE(lp)
    val lightSE = shiftNW(lp)
    val emptyNW = shiftSE(notOccupied)
    val emptyNE = shiftSW(notOccupied)
    val emptySW = shiftNE(notOccupied)
    val emptySE = shiftNW(notOccupied)
    val kingNW = shiftSE(k)
    val kingNE = shiftSW(k)
    val kingSW = shiftNE(k)
    val kingSE = shiftNW(k)

    val potentialAttacks = innerSquares & notOccupied

    val darkAttacks = potentialAttacks &
      ((darkSW & emptyNE) | (darkSE & emptyNW) | (darkNW & kingNW & emptySE) | (darkNE & kingNE & emptySW))
    
    val lightAttacks = potentialAttacks &
      ((lightNW & emptySE) | (lightNE & emptySW) | (lightSW & kingSW & emptyNE) | (lightSE & kingSE & emptyNW))
    
    val safeForDark = notOccupied & (~lightAttacks)
    val safeForLight = notOccupied & (~darkAttacks)

    if(probe != null) {
      probe.potentialAttacks = potentialAttacks
      probe.darkAttacks = darkAttacks
      probe.lightAttacks = lightAttacks
      probe.safeForDark = safeForDark
      probe.safeForLight = safeForLight
    }

    var darkMen = 0
    var darkKings = 0
    var lightMen = 0
    var lightKings = 0
    var darkMaterial = 0
    var lightMaterial = 0
    var emptySquares = 0


    var currentSquareMask = 1
    var i = 0
    while(i < 32) {
      if(((dp >>> i) & 1) != 0) {
        if(((k >>> i) & 1) != 0) {
          darkKings += 1
          darkMaterial += King
        } else {
          darkMen += 1
          darkMaterial += Man
        }
      } else if(((dp >>> i) & 1) != 0) {
        if (((k >>> i) & 1) != 0) {
          lightKings += 1
          lightMaterial += King
        } else {
          lightMen += 1
          lightMaterial += Man
        }
      } else {
        emptySquares |= currentSquareMask
      }
      i += 1

      currentSquareMask = currentSquareMask << 1
    }

    var result = 0
    if(color == DARK) {
      result += darkMaterial - lightMaterial
    } else {
      result += lightMaterial - darkMaterial
    }

    if(turnToPlay == color) result += TurnAdvantageBonus

    if(rulesSettings.giveaway) -result else result
  }

}

class DefaultEvaluatorTestProbe {
  var potentialAttacks: Int = 0
  var darkAttacks: Int = 0
  var lightAttacks: Int = 0
  var safeForDark: Int = 0
  var safeForLight: Int = 0
}