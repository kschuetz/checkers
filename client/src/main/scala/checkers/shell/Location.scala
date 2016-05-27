package checkers.shell

sealed trait Location

object Location {

  case object Sandbox extends Location

}