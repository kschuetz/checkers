package checkers

package object logger {
  lazy val log4JavaScript: Log4JavaScript = GlobalLog4JavaScript()

  lazy val log: Logger = LoggerFactory.getLogger("Log")

  lazy val animations: Logger = LoggerFactory.getLogger("Animations", log4JavaScript.Level.OFF)

  lazy val inputEvents: Logger = LoggerFactory.getLogger("InputEvents")

  lazy val gameDriver: Logger = LoggerFactory.getLogger("GameDriver")

  lazy val computerPlayer: Logger = LoggerFactory.getLogger("ComputerPlayer")
}
