package checkers.util

import scala.annotation.tailrec

import scala.scalajs.js

/**
  * The implementation of most of this class was copied directly from java.util.Random in the
  * Scala.js standard library.  This version has been modified to be stateless.
  */
class Random(private val seedHi: Int,
             private val seedLo: Int) {

  def seed: Long = (seedHi.toLong << 24) | (seedLo & ((1 << 24) - 1))

  protected def next(bits: Int): (Int, Random) = {

    @inline
    def rawToInt(x: Double): Int =
      (x.asInstanceOf[js.Dynamic] | 0.asInstanceOf[js.Dynamic]).asInstanceOf[Int]

    @inline
    def _24msbOf(x: Double): Int = rawToInt(x / (1 << 24).toDouble)

    @inline
    def _24lsbOf(x: Double): Int = rawToInt(x) & ((1 << 24) - 1)

    // seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1)

    val twoPow24 = (1 << 24).toDouble

    val oldSeedHi = seedHi
    val oldSeedLo = seedLo

    val mul = 0x5DEECE66DL
    val mulHi = (mul >>> 24).toInt
    val mulLo = mul.toInt & ((1 << 24) - 1)

    val loProd = oldSeedLo.toDouble * mulLo.toDouble + 0xB
    val hiProd = oldSeedLo.toDouble * mulHi.toDouble + oldSeedHi.toDouble * mulLo.toDouble
    val newSeedHi =
      (_24msbOf(loProd) + _24lsbOf(hiProd)) & ((1 << 24) - 1)
    val newSeedLo =
      _24lsbOf(loProd)

    val result32 = (newSeedHi << 8) | (newSeedLo >> 16)
    val result = result32 >>> (32 - bits)
    val newState = new Random(newSeedHi, newSeedLo)
    (result, newState)
  }

  def nextDouble(): (Double, Random) = {
    val (a, s1) = next(26)
    val (b, newState) = s1.next(27)
    val result = ((a.toDouble * (1L << 27).toDouble) + b.toDouble) / (1L << 53).toDouble
    (result, newState)
  }

  def nextBoolean(): (Boolean, Random) = {
    val (result, newState) = next(1)
    (result != 0, newState)
  }

  def nextInt(): (Int, Random) = next(32)

  def nextInt(n: Int): (Int, Random) = {
    if (n <= 0) {
      throw new IllegalArgumentException("n must be positive")
    } else if ((n & -n) == n) { // i.e., n is a power of 2
      val (result, newState) = next(31)
      (result >> Integer.numberOfLeadingZeros(n), newState)
    } else {
      @tailrec
      def loop(state: Random): (Int, Random) = {
        val (bits, nextState) = next(31)
        val value = bits % n
        if (bits - value + (n-1) < 0) loop(nextState)
        else (value, nextState)
      }

      loop(this)
    }
  }
}

object Random {
  def apply(seedIn: Long): Random = {
    val seed = (seedIn ^ 0x5DEECE66DL) & ((1L << 48) - 1)
    val seedHi = (seed >>> 24).toInt
    val seedLo = seed.toInt & ((1 << 24) - 1)
    new Random(seedHi, seedLo)
  }

  def apply(): Random = apply(randomSeed())

  /** Generate a random long from JS RNG to seed a new Random */
  private def randomSeed(): Long =
    (randomInt().toLong << 32) | (randomInt().toLong & 0xffffffffL)

  private def randomInt(): Int =
    (Math.floor(js.Math.random() * 4294967296.0) - 2147483648.0).toInt

}
