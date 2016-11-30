package checkers.core

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
}

trait ApplicationSettingsProvider {
  def applicationSettings: ApplicationSettings
}

object DefaultApplicationSettings extends ApplicationSettings {
  val ClockUpdateIntervalMillis: Int = 667

  val PowerSaveIdleThresholdSeconds: Option[Int] = Some(300)   // 5 minutes
}

object DefaultApplicationSettingsProvider extends ApplicationSettingsProvider {
  def applicationSettings: ApplicationSettings = DefaultApplicationSettings
}