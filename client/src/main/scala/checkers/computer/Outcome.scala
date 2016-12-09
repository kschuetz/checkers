package checkers.computer

import checkers.consts._

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
  val Best: EncodedOutcome = ENCODEOUTCOME(WIN, 0)
  val Worst: EncodedOutcome = ENCODEOUTCOME(LOSS, 0)
}