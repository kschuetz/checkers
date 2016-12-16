package checkers.core

import scala.scalajs.LinkingInfo

trait ApplicationSettings {
  def ClockUpdateIntervalMillis: Int

  // The number of seconds of idle time (i.e. no human activity) before
  // clock tick events are suspended.  The clock will remain active, it
  // will just not render every second while in power save mode.
  // Rendering will resume once human activity resumes.
  //
  // The purpose of this is to prevent unnecessary wasting of power
  // in case the user moves the game to a background tab for a while.
  def PowerSaveIdleThresholdSeconds: Option[Int]

  def ShowEvaluationScoreInTopChrome: Boolean
}

trait ApplicationSettingsProvider {
  def applicationSettings: ApplicationSettings
}

class DefaultApplicationSettings extends ApplicationSettings {
  val ClockUpdateIntervalMillis: Int = 667

  val PowerSaveIdleThresholdSeconds: Option[Int] = Some(300)   // 5 minutes

  val ShowEvaluationScoreInTopChrome: Boolean = LinkingInfo.developmentMode
}

case class ConstantApplicationSettingsProvider(applicationSettings: ApplicationSettings) extends ApplicationSettingsProvider

object ApplicationSettingsProvider {
  def apply(applicationSettings: ApplicationSettings): ApplicationSettingsProvider =
    ConstantApplicationSettingsProvider(applicationSettings)
}