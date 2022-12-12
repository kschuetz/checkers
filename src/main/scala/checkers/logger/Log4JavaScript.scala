package checkers.logger

import scala.scalajs.js
import js.annotation._

/**
  * Facade for functions in log4javascript that we need
  */

@js.native
trait Level extends js.Object

@js.native
trait JSLogger extends js.Object {
  def addAppender(appender: Appender): Unit = js.native

  def removeAppender(appender: Appender): Unit = js.native

  def removeAllAppenders(appender: Appender): Unit = js.native

  def setLevel(level: Level): Unit = js.native

  def getLevel: Level = js.native

  def trace(msg: String, error: js.UndefOr[js.Error]): Unit = js.native

  def debug(msg: String, error: js.UndefOr[js.Error]): Unit = js.native

  def info(msg: String, error: js.UndefOr[js.Error]): Unit = js.native

  def warn(msg: String, error: js.UndefOr[js.Error]): Unit = js.native

  def error(msg: String, error: js.UndefOr[js.Error]): Unit = js.native

  def fatal(msg: String, error: js.UndefOr[js.Error]): Unit = js.native

  def trace(msg: String): Unit = js.native

  def debug(msg: String): Unit = js.native

  def info(msg: String): Unit = js.native

  def warn(msg: String): Unit = js.native

  def error(msg: String): Unit = js.native

  def fatal(msg: String): Unit = js.native
}

@js.native
trait Layout extends js.Object

@js.native
trait Appender extends js.Object {
  def setLayout(layout: Layout): Unit = js.native

  def setThreshold(level: Level): Unit = js.native
}

/*
  We are relying on loggingSetup.js to attach log4javascript to the global scope.
  We can't use @JSImport here because the tests won't compile.
 */
object GlobalLog4JavaScript {
  def apply(): Log4JavaScript = js.Dynamic.global.window.log4javascript.asInstanceOf[Log4JavaScript]
}

@js.native
@JSGlobal
class JsonLayout extends Layout

@js.native
@JSGlobal
class BrowserConsoleAppender extends Appender

@js.native
@JSGlobal
class PopUpAppender extends Appender

@js.native
@JSGlobal
class AjaxAppender(url: String) extends Appender {
  def addHeader(header: String, value: String): Unit = js.native
}

@js.native
@JSGlobal
class Log4JavaScript extends js.Object {
  def getLogger(name: js.UndefOr[String]): JSLogger = js.native

  def setEnabled(enabled: Boolean): Unit = js.native

  def isEnabled: Boolean = js.native

  @js.native
  object Level extends js.Object {
    val ALL: Level = js.native
    val TRACE: Level = js.native
    val DEBUG: Level = js.native
    val INFO: Level = js.native
    val WARN: Level = js.native
    val ERROR: Level = js.native
    val FATAL: Level = js.native
    val OFF: Level = js.native
  }

  def createJsonLayout(): JsonLayout = js.native

  def createBrowserConsoleAppender(): BrowserConsoleAppender = js.native

  def createPopUpAppender(): PopUpAppender = js.native

  def createAjaxAppender(url: String): AjaxAppender = js.native

}

class L4JSLogger(jsLogger: JSLogger) extends Logger {

  private def undefOrError(e: Exception): js.UndefOr[js.Error] = {
    if (e == null)
      js.undefined
    else
      e.asInstanceOf[js.Error]
  }

  override def trace(msg: String, e: Exception): Unit = jsLogger.trace(msg, undefOrError(e))

  override def trace(msg: String): Unit = jsLogger.trace(msg)

  override def debug(msg: String, e: Exception): Unit = jsLogger.debug(msg, undefOrError(e))

  override def debug(msg: String): Unit = jsLogger.debug(msg)

  override def info(msg: String, e: Exception): Unit = jsLogger.info(msg, undefOrError(e))

  override def info(msg: String): Unit = jsLogger.info(msg)

  override def warn(msg: String, e: Exception): Unit = jsLogger.warn(msg, undefOrError(e))

  override def warn(msg: String): Unit = jsLogger.warn(msg)

  override def error(msg: String, e: Exception): Unit = jsLogger.error(msg, undefOrError(e))

  override def error(msg: String): Unit = jsLogger.error(msg)

  override def fatal(msg: String, e: Exception): Unit = jsLogger.fatal(msg, undefOrError(e))

  override def fatal(msg: String): Unit = jsLogger.fatal(msg)

  def setLevel(level: Level): Unit = jsLogger.setLevel(level)

}
