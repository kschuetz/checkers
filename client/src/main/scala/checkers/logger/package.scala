package checkers

package object logger {
  val log = LoggerFactory.getLogger("Log")

  val animations = LoggerFactory.getLogger("Animations", Level.DEBUG)

}
