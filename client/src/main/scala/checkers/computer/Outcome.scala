package checkers.computer

import checkers.consts._

/*
 Rules for Outcomes:

 Value:
   WIN > SCORE > DRAW > LOSS

 When comparing the value of equal types:
   WIN:   lower depth is better
   LOSS:  higher depth is better
   DRAW:  higher depth is better
   SCORE: higher score is better

 Negating:

   -WIN(depth)   == LOSS(depth)
   -LOSS(depth)  == WIN(depth)
   -DRAW(depth)  == DRAW(depth)     (Negating a DRAW results in itself, as a draw is equally unfavorable to both sides)
   -SCORE(value) == SCORE(-value)

 */

class Outcome(val underlying: EncodedOutcome) extends AnyVal {

  def outcomeType: OutcomeType = OUTCOMETYPE(underlying)

  def outcomeValue: OutcomeValue = OUTCOMEVALUE(underlying)

  def negate: Outcome = {
    val outcomeType = OUTCOMETYPE(underlying)
    val value = OUTCOMEVALUE(underlying)
    if (outcomeType == WIN) new Outcome(ENCODEOUTCOME(LOSS, value))
    else if (outcomeType == LOSS) new Outcome(ENCODEOUTCOME(WIN, value))
    else if (outcomeType == SCORE) new Outcome(ENCODEOUTCOME(SCORE, -value))
    else this // draw:  negating equals itself
  }

  def >(other: Outcome): Boolean = {
    val myType = OUTCOMETYPE(underlying)
    val otherType = OUTCOMETYPE(other.underlying)
    (myType > otherType) || ((myType == otherType) &&
      (if (myType == WIN) OUTCOMEVALUE(underlying) < OUTCOMEVALUE(other.underlying)
      else OUTCOMEVALUE(underlying) > OUTCOMEVALUE(other.underlying)))
  }

  def >=(other: Outcome): Boolean = {
    val myType = OUTCOMETYPE(underlying)
    val otherType = OUTCOMETYPE(other.underlying)
    (myType > otherType) || ((myType == otherType) &&
      (if (myType == WIN) OUTCOMEVALUE(underlying) <= OUTCOMEVALUE(other.underlying)
      else OUTCOMEVALUE(underlying) >= OUTCOMEVALUE(other.underlying)))
  }

  def <(other: Outcome): Boolean = {
    val myType = OUTCOMETYPE(underlying)
    val otherType = OUTCOMETYPE(other.underlying)
    (myType < otherType) || ((myType == otherType) &&
      (if (myType == WIN) OUTCOMEVALUE(underlying) > OUTCOMEVALUE(other.underlying)
      else OUTCOMEVALUE(underlying) < OUTCOMEVALUE(other.underlying)))
  }

  def <=(other: Outcome): Boolean = {
    val myType = OUTCOMETYPE(underlying)
    val otherType = OUTCOMETYPE(other.underlying)
    (myType < otherType) || ((myType == otherType) &&
      (if (myType == WIN) OUTCOMEVALUE(underlying) >= OUTCOMEVALUE(other.underlying)
      else OUTCOMEVALUE(underlying) <= OUTCOMEVALUE(other.underlying)))
  }
}

object Outcome {
  val Best: Outcome = new Outcome(ENCODEOUTCOME(WIN, 0))
  val Worst: Outcome = new Outcome(ENCODEOUTCOME(LOSS, 0))
}