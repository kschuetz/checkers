package checkers.core

import checkers.components.dialog.NewGameDialog
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

  private case class PlayerChoiceNode(programId: Option[String], displayName: String)

  private sealed trait VariationChoice {
    def displayName: String
    def applyToRuleSettings(rulesSettings: RulesSettings): RulesSettings
  }
  private object VariationChoice {
    case object Standard
  }

  private lazy val variationChoices = Vector()



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
//      val props = NewGameDialog.Props(playerChoices = Vector.empty,
//        variationChoices = Vector("Standard", "Giveaway"),
//        initialDarkPlayer = 0,
//        initialLightPlayer = 0
//        )
    }

    /*
    case class Props(playerChoices: Vector[String],
                   variationChoices: Vector[String],
                   initialDarkPlayer: Int,
                   initialLightPlayer: Int,
                   initialPlaysFirst: Color,
                   initialVariationIndex: Int,
                   callbacks: NewGameDialogCallbacks) {
     */

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