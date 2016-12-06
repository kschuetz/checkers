package checkers.core

case class PlayerState(opaque: Opaque,
                       mentorOpaque: Option[Opaque],
                       clock: Double)