package checkers.computer

import checkers.consts._
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

  // inner to outer
  private val layerBonus = Vector(0, 1, 2, 3)

  private val rankBonusMan = Vector(6, 1, 2, 3, 4, 5, 6, 0)
  private val rankBonusKing = Vector(6, 0, 0, 0, 0, 0, 0, 0)

  private val Man = 100
  private val King = 150

  val (darkM, darkK, lightM, lightK) = {
    val layerBonuses = Vector(
      3, 3, 3, 3,
      2, 2, 2, 3,
      2, 1, 1, 3,
      3, 1, 0, 2,
      2, 0, 1, 3,
      3, 1, 1, 2,
      2, 2, 2, 3,
      3, 3, 3, 3).map(layerBonus)

    val darkBonusMan = (0 to 31).map(i => rankBonusMan(i / 4))
    val lightBonusMan = darkBonusMan.reverse
    val darkBonusKing = (0 to 31).map(i => rankBonusKing(i / 4))
    val lightBonusKing = darkBonusKing.reverse

    val darkM = new Int32Array(32)
    val darkK = new Int32Array(32)
    val lightM = new Int32Array(32)
    val lightK = new Int32Array(32)
    for (i <- 0 to 31) {
      darkM(i) = Man + layerBonuses(i) + darkBonusMan(i)
      darkK(i) = King + layerBonuses(i) + darkBonusKing(i)
      lightM(i) = Man + layerBonuses(i) + lightBonusMan(i)
      lightK(i) = King + layerBonuses(i) + lightBonusKing(i)
    }
    (darkM, darkK, lightM, lightK)
  }


  def evaluate(color: Color, turnToPlay: Color, board: BoardStateRead): Int = {
    val k = board.kings
    val lp = board.lightPieces
    val dp = board.darkPieces
    var darkMen = 0
    var darkKings = 0
    var lightMen = 0
    var lightKings = 0
    var darkMaterial = 0
    var lightMaterial = 0

    var i = 0
    while(i < 32) {
      if(((dp >>> i) & 1) != 0) {
        if(((k >>> i) & 1) != 0) {
          darkKings += 1
          darkMaterial += darkK(i)
        } else {
          darkMen += 1
          darkMaterial += darkM(i)
        }
      } else if(((dp >>> i) & 1) != 0) {
        if (((k >>> i) & 1) != 0) {
          lightKings += 1
          lightMaterial += lightK(i)
        } else {
          lightMen += 1
          lightMaterial += lightM(i)
        }
      }
      i += 1
    }

    var result = 0
    if(color == DARK) {
      result += darkMaterial - lightMaterial
    } else {
      result += lightMaterial - darkMaterial
    }

    if(turnToPlay == color) result += 3

    if(rulesSettings.giveaway) -result else result
  }



}