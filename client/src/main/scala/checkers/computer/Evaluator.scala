package checkers.computer

import checkers.consts._
import checkers.core.{BoardStateRead, RulesSettings}

import scala.scalajs.js.typedarray.Int32Array


class Evaluator(rulesSettings: RulesSettings) {

  //           28  29  30  31
  //         24  25  26  27
  //           20  21  22  23
  //         16  17  18  19
  //           12  13  14  15
  //         08  09  10  11
  //           04  05  06  07
  //         00  01  02  03

  // inner to outer
  private val layerWeights = Vector(45, 48, 51, 54)
  
  private val rankBiasMan = Vector(0, 1, 2, 3, 4, 5, 6, 0)
  private val rankBiasKing = Vector(0, 2, 2, 2, 2, 2, 2, 0)

  private val Man = 72
  private val King = 100

  val (darkM, darkK, lightM, lightK) = {
    val baseWeights = Vector(
      3, 3, 3, 3,
      2, 2, 2, 3,
      2, 1, 1, 3,
      3, 1, 0, 2,
      2, 0, 1, 3,
      3, 1, 1, 2,
      2, 2, 2, 3,
      3, 3, 3, 3).map(layerWeights)

    val darkBiasMan = (0 to 31).map(i => rankBiasMan(i / 4))
    val lightBiasMan = darkBiasMan.reverse
    val darkBiasKing = (0 to 31).map(i => rankBiasKing(i / 4))
    val lightBiasKing = darkBiasKing.reverse

    val darkM = new Int32Array(32)
    val darkK = new Int32Array(32)
    val lightM = new Int32Array(32)
    val lightK = new Int32Array(32)
    for (i <- 0 to 31) {
      darkM(i) = Man * (baseWeights(i) + darkBiasMan(i))
      darkK(i) = King * (baseWeights(i) + darkBiasKing(i))
      lightM(i) = Man * (baseWeights(i) + lightBiasMan(i))
      lightK(i) = King * (baseWeights(i) + lightBiasKing(i))
    }
    (darkM, darkK, lightM, lightK)
  }

  def evaluate(player: Color, board: BoardStateRead): Int = {
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

    0
  }



}