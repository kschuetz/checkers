package checkers.core

import checkers.computer.ProgramRegistry
import checkers.core.tables.TablesModule
import checkers.persistence.NewGameSettingsPersister
import japgolly.scalajs.react.Callback
import org.scalajs.dom

class Application(programRegistry: ProgramRegistry,
                  tablesModule: TablesModule,
                  animationSettings: AnimationSettings,
                  newGameSettingsPersister: NewGameSettingsPersister,
                  gameFactory: GameFactory,
                  makeGameLogicModule: GameLogicModuleFactory)  {

  class Session(gameHost: dom.Node, dialogHost: dom.Node) extends ApplicationCallbacks {
    var game: Option[Game] = None

    def startNewGame(settings: NewGameSettings): Unit = {
      stopGame()
      val newGame = gameFactory.create(settings, gameHost)
      game = Some(newGame)
      newGame.initApplicationCallbacks(this)
      newGame.run()
    }

    def run(): Unit = {
      val newGameSettings = newGameSettingsPersister.loadNewGameSettings.getOrElse(NewGameSettings.default)
      startNewGame(newGameSettings)
    }

    def stopGame(): Unit = {
      game.foreach(_.stop())
      game = None
    }

    override def onNewGameButtonClicked: Callback = Callback {
      println("New game button clicked")
    }
  }

  def start(gameHost: dom.Node, dialogHost: dom.Node): Unit = {
    val session = new Session(gameHost, dialogHost)
    session.run()
  }


}