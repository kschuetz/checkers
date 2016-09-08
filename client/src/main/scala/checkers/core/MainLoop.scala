package checkers.core

import checkers.computer.ProgramRegistry
import checkers.core.tables.TablesModule
import checkers.persistence.NewGameSettingsPersister
import org.scalajs.dom

class MainLoop(programRegistry: ProgramRegistry,
               tablesModule: TablesModule,
               animationSettings: AnimationSettings,
               newGameSettingsPersister: NewGameSettingsPersister,
               gameFactory: GameFactory,
               makeGameLogicModule: GameLogicModuleFactory) {

  def start(gameHost: dom.Node, dialogHost: dom.Node): Unit = {
    var game: Game = null

    def runGame(): Unit = {
      val newGameSettings = newGameSettingsPersister.loadNewGameSettings.getOrElse(NewGameSettings.default)
      game = gameFactory.create(newGameSettings, gameHost)
      game.run()
    }

    runGame()

  }

}