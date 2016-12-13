package checkers.logger

import scala.annotation.elidable
import scala.annotation.elidable._

object NullLogger extends Logger {

  @elidable(FINEST)
  override def trace(msg: String, e: Exception): Unit = { }

  @elidable(FINEST)
  override def trace(msg: String): Unit = { }

  @elidable(FINE)
  override def debug(msg: String, e: Exception): Unit = { }

  @elidable(FINE)
  override def debug(msg: String): Unit = { }

  @elidable(INFO)
  override def info(msg: String, e: Exception): Unit = { }

  @elidable(INFO)
  override def info(msg: String): Unit = { }

  @elidable(WARNING)
  override def warn(msg: String, e: Exception): Unit = { }

  @elidable(WARNING)
  override def warn(msg: String): Unit = { }

  @elidable(SEVERE)
  override def error(msg: String, e: Exception): Unit = { }

  @elidable(SEVERE)
  override def error(msg: String): Unit = { }

  @elidable(SEVERE)
  override def fatal(msg: String, e: Exception): Unit = { }

  @elidable(SEVERE)
  override def fatal(msg: String): Unit = { }
}