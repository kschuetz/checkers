package checkers.core

import checkers.consts._

sealed trait DrawStatus

case object NoDraw extends DrawStatus

case class DrawProposed(color: Color, endTurnIndex: Int) extends DrawStatus