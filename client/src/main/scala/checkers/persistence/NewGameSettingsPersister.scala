package checkers.persistence

import checkers.core.NewGameSettings

trait NewGameSettingsPersister {
  def loadNewGameSettings: Option[NewGameSettings]

  def saveNewGameSettings(settings: NewGameSettings): Unit
}

object NullNewGameSettingsPersister extends NewGameSettingsPersister {
  override def loadNewGameSettings: Option[NewGameSettings] = None

  override def saveNewGameSettings(settings: NewGameSettings): Unit = { }
}

object LocalStorageNewGameSettingsPersister extends NewGameSettingsPersister {
  override def loadNewGameSettings: Option[NewGameSettings] = None

  override def saveNewGameSettings(settings: NewGameSettings): Unit = None
}