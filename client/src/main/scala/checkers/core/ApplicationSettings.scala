package checkers.core

trait ApplicationSettings {
  def PowerSaveIdleThresholdSeconds: Option[Int]
}

trait ApplicationSettingsProvider {
  def applicationSettings: ApplicationSettings
}

object DefaultApplicationSettings extends ApplicationSettings {
  val PowerSaveIdleThresholdSeconds: Option[Int] = Some(600)
}

object DefaultApplicationSettingsProvider extends ApplicationSettingsProvider {
  def applicationSettings: ApplicationSettings = DefaultApplicationSettings
}