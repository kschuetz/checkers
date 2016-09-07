package checkers.components.dialog

import checkers.consts._
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB}


object NewGameDialog {

  sealed trait Result

  case object Cancel extends Result

  case class Ok(darkPlayerIndex: Int,
                lightPlayerIndex: Int,
                playsFirst: Color,
                variationIndex: Int) extends Result


  trait NewGameDialogCallbacks {
    def handleNewGameDialogResult(result: Result): Callback
  }

  case class PlayerChangeEvent(color: Color,
                               playerIndex: Int)

  case class VariationChangeEvent(variationIndex: Int)

  case class State(darkPlayerIndex: Int,
                   lightPlayerIndex: Int,
                   playsFirst: Color,
                   variationIndex: Int) {
    def withPlayerChange(event: PlayerChangeEvent): State =
      if(event.color == DARK) {
        copy(darkPlayerIndex = event.playerIndex)
      } else {
        copy(lightPlayerIndex = event.playerIndex)
      }

    def withVariationChange(event: VariationChangeEvent): State =
      copy(variationIndex = event.variationIndex)

    def withPlaysFirst(color: Color): State =
      copy(playsFirst = color)
  }

  case class Props(playerChoices: Vector[String],
                   variationChoices: Vector[String],
                   initialDarkPlayer: Int,
                   initialLightPlayer: Int,
                   initialPlaysFirst: Color,
                   initialVariationIndex: Int,
                   callbacks: NewGameDialogCallbacks) {
    def initialState: State = State(initialDarkPlayer, initialLightPlayer, initialPlaysFirst, initialVariationIndex)
  }




  trait PlayerPanelCallbacks {
    def handlePlayerChanged(event: PlayerChangeEvent): Callback

    def handlePlaysFirstChanged(color: Color): Callback
  }

  case class PlayerSettingsPanelProps(color: Color,
                                      playerChoices: Vector[String],
                                      playerIndex: Int,
                                      playsFirst: Boolean,
                                      callbacks: PlayerPanelCallbacks)

  class PlayerSettingsPanelBackend($: BackendScope[PlayerSettingsPanelProps, Unit]) {
    def render(props: PlayerSettingsPanelProps) = {
      <.div()
    }
  }

  private val PlayerSettingsPanel = ReactComponentB[PlayerSettingsPanelProps]("PlayerSettingsPanel")
    .renderBackend[PlayerSettingsPanelBackend]
    .build




  trait GeneralSettingsPanelCallbacks {
    def handleVariationChanged(event: VariationChangeEvent): Callback
  }

  case class GeneralSettingsPanelProps(variationChoices: Vector[String],
                                       variationIndex: Int,
                                       callbacks: GeneralSettingsPanelCallbacks)

  class GeneralSettingsPanelBackend($: BackendScope[GeneralSettingsPanelProps, Unit]) {
    def render(props: GeneralSettingsPanelProps) = {
      <.div()
    }
  }

  private val GeneralSettingsPanel = ReactComponentB[GeneralSettingsPanelProps]("GeneralSettingsPanel")
    .renderBackend[GeneralSettingsPanelBackend]
    .build


  trait DialogButtonsCallbacks {
    def handleOkClicked: Callback

    def handleCancelClicked: Callback
  }

  class DialogButtonsBackend($: BackendScope[DialogButtonsCallbacks, Unit]) {
    def render(props: DialogButtonsCallbacks) = {
      <.div()
    }
  }

  private val DialogButtons = ReactComponentB[DialogButtonsCallbacks]("DialogButtons")
    .renderBackend[DialogButtonsBackend]
    .build


  class NewGameDialogBackend($: BackendScope[Props, State]) extends PlayerPanelCallbacks
    with DialogButtonsCallbacks
    with GeneralSettingsPanelCallbacks {

    def render(props: Props, state: State) = {
      val darkPlayerProps = PlayerSettingsPanelProps(color = DARK,
        playerChoices = props.playerChoices,
        playerIndex = state.darkPlayerIndex,
        playsFirst = state.playsFirst == DARK,
        callbacks = this)

      val darkPlayerPanel = PlayerSettingsPanel(darkPlayerProps)

      val lightPlayerProps = darkPlayerProps.copy(
        color = LIGHT,
        playerIndex = state.lightPlayerIndex,
        playsFirst = state.playsFirst != DARK)

      val lightPlayerPanel = PlayerSettingsPanel(lightPlayerProps)

      val generalSettingsPanelProps = GeneralSettingsPanelProps(variationChoices = props.variationChoices,
        variationIndex = state.variationIndex,
        callbacks = this
      )

      val generalSettingsPanel = GeneralSettingsPanel(generalSettingsPanelProps)

      val dialogButtons = DialogButtons(this)

      <.div(
        ^.id := "new-game-dialog",
        ^.`class` := "modal-dialog",
        <.div(
          darkPlayerPanel,
          lightPlayerPanel,
          generalSettingsPanel,
          dialogButtons
        )
      )
    }

    def handlePlayerChanged(event: PlayerChangeEvent) = $.modState(_.withPlayerChange(event))

    def handlePlaysFirstChanged(color: Color) = $.modState(_.withPlaysFirst(color))

    def handleVariationChanged(event: VariationChangeEvent) = $.modState(_.withVariationChange(event))

    def handleOkClicked: Callback = for {
      props <- $.props
      state <- $.state
      data = Ok(darkPlayerIndex = state.darkPlayerIndex,
        lightPlayerIndex = state.lightPlayerIndex,
        playsFirst = state.playsFirst,
        variationIndex = state.variationIndex)
      result <- props.callbacks.handleNewGameDialogResult(data)
    } yield result

    def handleCancelClicked: Callback = for {
      props <- $.props
      data = Cancel
      result <- props.callbacks.handleNewGameDialogResult(data)
    } yield result


  }

  val component = ReactComponentB[Props]("NewGameDialog")
    .initialState_P[State](_.initialState)
    .renderBackend[NewGameDialogBackend]
    .build

  def apply(props: Props) = component(props)

}