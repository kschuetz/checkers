package checkers.core

import checkers.computer.ProgramRegistry
import checkers.core.tables.TablesModule
import checkers.persistence.NewGameSettingsPersister
import org.scalajs.dom

class Application(programRegistry: ProgramRegistry,
                  tablesModule: TablesModule,
                  animationSettings: AnimationSettings,
                  newGameSettingsPersister: NewGameSettingsPersister,
                  gameFactory: GameFactory,
                  makeGameLogicModule: GameLogicModuleFactory) {

  class Session(gameHost: dom.Node, dialogHost: dom.Node) {
    var game: Game = _
    def run(): Unit = {
      val newGameSettings = newGameSettingsPersister.loadNewGameSettings.getOrElse(NewGameSettings.default)
      game = gameFactory.create(newGameSettings, gameHost)
      game.run()
    }
  }

  def start(gameHost: dom.Node, dialogHost: dom.Node): Unit = {
    val session = new Session(gameHost, dialogHost)
    session.run()
  }

}