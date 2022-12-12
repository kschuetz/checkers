package checkers.core

// DrawStatus must not contain any mutable state
trait DrawStatus extends Serializable {
  def isDraw: Boolean

  def turnsRemainingHint: Option[Int]
}

object NullDrawStatus extends DrawStatus {
  override def isDraw: Boolean = false

  override def turnsRemainingHint: Option[Int] = None
}