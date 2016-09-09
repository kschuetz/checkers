package checkers.core

import checkers.components.dialog.{NewGameDialog, PlayerChoice}
import checkers.components.dialog.NewGameDialog.{NewGameDialogCallbacks, Result}
import checkers.computer.ProgramRegistry
import checkers.core.tables.TablesModule
import checkers.persistence.NewGameSettingsPersister
import japgolly.scalajs.react.{Callback, ReactDOM}
import org.scalajs.dom

class Application(programRegistry: ProgramRegistry,
                  tablesModule: TablesModule,
                  animationSettings: AnimationSettings,
                  newGameSettingsPersister: NewGameSettingsPersister,
                  gameFactory: GameFactory,
                  makeGameLogicModule: GameLogicModuleFactory)  {

  private lazy val playerChoices: Vector[PlayerChoice] = {
    val computerPlayers = programRegistry.entries.sortWith {
      case (a, b) => a.difficultyLevel < b.difficultyLevel || a.name < b.name
    } map { entry =>
      PlayerChoice(entry.name, Some(entry.uniqueId), entry.difficultyLevel)
    }
    PlayerChoice.human +: computerPlayers
  }

  private def findPlayerChoiceIndexById(programId: Option[String]): Int = {
    val result = playerChoices.indexWhere(_.programId == programId)
    if(result >= 0) result else 0
  }


  class Session(gameHost: dom.Node, dialogHost: dom.Node) extends ApplicationCallbacks with NewGameDialogCallbacks {
    var game: Option[Game] = None
    var lastUsedSettings: Option[NewGameSettings] = None
    var newGameDialogProps: Option[NewGameDialog.Props] = None

    def startNewGame(settings: NewGameSettings): Unit = {
      stopGame()
      val newGame = gameFactory.create(settings, gameHost)
      game = Some(newGame)
      lastUsedSettings = Some(settings)
      newGameSettingsPersister.saveNewGameSettings(settings)
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

    override val onNewGameButtonClicked: Callback = Callback {
      println("New game button clicked")
      openNewGameDialog()
    }

    override def onNewGameDialogResult(result: Result): Callback = Callback {
      println("New game dialog result:")
      println(result)
      closeNewGameDialog()
    }

    private def openNewGameDialog(): Unit = {
      val settings = lastUsedSettings.getOrElse(NewGameSettings.default)

      val props = NewGameDialog.Props(playerChoices = playerChoices,
        variationChoices = Variation.all,
        initialDarkPlayer = findPlayerChoiceIndexById(settings.darkProgramId),
        initialLightPlayer = findPlayerChoiceIndexById(settings.lightProgramId),
        initialPlaysFirst = settings.rulesSettings.playsFirst,
        initialVariationIndex = math.max(0, Variation.all.indexOf(settings.rulesSettings.variation)),
        callbacks = this)

      println(props)

      val dialog = NewGameDialog(props)

      ReactDOM.render(dialog, dialogHost)
    }

    private def closeNewGameDialog(): Unit = {
      newGameDialogProps = None
      ReactDOM.unmountComponentAtNode(dialogHost)
    }
  }

  def start(gameHost: dom.Node, dialogHost: dom.Node): Unit = {
    val session = new Session(gameHost, dialogHost)
    session.run()
  }


}