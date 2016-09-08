package checkers.persistence

import checkers.core.NewGameSettings
import org.scalajs.dom

import scala.util.Try

trait NewGameSettingsPersister {
  def loadNewGameSettings: Option[NewGameSettings]

  def saveNewGameSettings(settings: NewGameSettings): Unit
}

object NullNewGameSettingsPersister extends NewGameSettingsPersister {
  override def loadNewGameSettings: Option[NewGameSettings] = None

  override def saveNewGameSettings(settings: NewGameSettings): Unit = { }
}

object LocalStorageNewGameSettingsPersister extends NewGameSettingsPersister {
  private val key = "new-game-settings"

  override def loadNewGameSettings: Option[NewGameSettings] = for {
    s <- Option(dom.window.localStorage.getItem(key))
    settings <- deserialize(s)
  } yield settings

  override def saveNewGameSettings(settings: NewGameSettings): Unit = {
    val data = serialize(settings)
    dom.window.localStorage.setItem(key, data)
  }

  private def deserialize(source: String): Option[NewGameSettings] = {
    import upickle.default._
    Try(read[NewGameSettings](source)).toOption
  }

  private def serialize(settings: NewGameSettings): String = {
    import upickle.default._
    write(settings)
  }


}