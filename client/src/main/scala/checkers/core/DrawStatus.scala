package checkers.core

import checkers.consts._

sealed trait DrawStatus

case object NoDraw extends DrawStatus

case class DrawProposed(side: Side, endTurnIndex: Int) extends DrawStatus