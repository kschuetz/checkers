package checkers

package object logger {
  val log = LoggerFactory.getLogger("Log")

  val animations = LoggerFactory.getLogger("Animations", Level.OFF)

  val inputEvents = LoggerFactory.getLogger("InputEvents")

  val gameDriver = LoggerFactory.getLogger("GameDriver")

  val computerPlayer = LoggerFactory.getPopUpLogger("ComputerPlayer")

}
