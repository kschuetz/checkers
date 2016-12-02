package checkers.core

import checkers.computer.ProgramRegistry
import checkers.core.tables.TablesModule
import checkers.logger
import checkers.persistence.NewGameSettingsPersister
import checkers.userinterface.dialog.NewGameDialog.{NewGameDialogCallbacks, Result}
import checkers.userinterface.dialog.{NewGameDialog, PlayerChoice}
import japgolly.scalajs.react.{Callback, ReactDOM}
import org.scalajs.dom

class Application(programRegistry: ProgramRegistry,
                  tablesModule: TablesModule,
                  animationSettings: AnimationSettings,
                  newGameSettingsPersister: NewGameSettingsPersister,
                  gameFactory: GameFactory,
                  makeGameLogicModule: GameLogicModuleFactory,
                  newGameDialog: NewGameDialog)  {

  private lazy val playerChoices: Vector[PlayerChoice] = {
    val computerPlayers = programRegistry.entries.sortWith {
      case (a, b) =>
        a.difficultyLevel < b.difficultyLevel || (a.difficultyLevel == b.difficultyLevel && a.name < b.name)
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
      game.foreach(_.humanActivity())
      openNewGameDialog()
    }

    override def onRotateBoardButtonClicked: Callback = Callback {
      game.foreach(_.humanActivity())
      game.foreach(_.rotateBoard())
    }

    override def onRushButtonClicked: Callback = Callback {
      game.foreach(_.rushComputer())
    }

    override def onNewGameDialogResult(result: Result): Callback = Callback {
      closeNewGameDialog()
      result match {
        case NewGameDialog.Cancel => ()
        case input: NewGameDialog.Ok =>
          val settings = makeNewGameSettings(input)
          startNewGame(settings)
      }
    }

    private def makeNewGameSettings(input: NewGameDialog.Ok): NewGameSettings = {
      val getPlayerChoice = input.playerChoices.lift
      val darkPlayerId = getPlayerChoice(input.darkPlayerIndex).flatMap(_.programId)
      val lightPlayerId = getPlayerChoice(input.lightPlayerIndex).flatMap(_.programId)
      val variation = input.variationChoices.lift(input.variationIndex).getOrElse(Variation.default)
      val rulesSettings = RulesSettings(playsFirst = input.playsFirst, variation = variation)
      NewGameSettings(rulesSettings = rulesSettings, darkProgramId = darkPlayerId, lightProgramId = lightPlayerId)
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

      logger.log.debug(props.toString)

      val dialog = newGameDialog.component(props)

      ReactDOM.render(dialog, dialogHost)
    }

    private def closeNewGameDialog(): Unit = {
      ReactDOM.unmountComponentAtNode(dialogHost)
    }
  }

  def start(gameHost: dom.Node, dialogHost: dom.Node): Unit = {
    val session = new Session(gameHost, dialogHost)
    session.run()
  }


}