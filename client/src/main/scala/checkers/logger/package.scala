package checkers

package object logger {
  val log: Logger = LoggerFactory.getLogger("Log")

  val animations: Logger = LoggerFactory.getLogger("Animations", Level.OFF)

  val inputEvents: Logger = LoggerFactory.getLogger("InputEvents")

  val gameDriver: Logger = LoggerFactory.getLogger("GameDriver")

  val computerPlayer: Logger = LoggerFactory.getPopUpLogger("ComputerPlayer")

}
