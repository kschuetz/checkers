package checkers.components.dialog

import checkers.consts._
import checkers.core.Variation
import checkers.util.StringUtils
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom

import scala.scalajs.js


object NewGameDialog {

  sealed trait Result

  case object Cancel extends Result

  case class Ok(playerChoices: Vector[PlayerChoice],
                variationChoices: Vector[Variation],
                darkPlayerIndex: Int,
                lightPlayerIndex: Int,
                playsFirst: Color,
                variationIndex: Int) extends Result


  trait NewGameDialogCallbacks {
    def onNewGameDialogResult(result: Result): Callback
  }

  case class PlayerChangeEvent(color: Color,
                               playerIndex: Int)

  case class VariationChangeEvent(variationIndex: Int)

  case class State(darkPlayerIndex: Int,
                   lightPlayerIndex: Int,
                   playsFirst: Color,
                   variationIndex: Int) {
    def withPlayerChange(event: PlayerChangeEvent): State =
      if (event.color == DARK) {
        copy(darkPlayerIndex = event.playerIndex)
      } else {
        copy(lightPlayerIndex = event.playerIndex)
      }

    def withVariationChange(event: VariationChangeEvent): State =
      copy(variationIndex = event.variationIndex)

    def withPlaysFirst(color: Color): State =
      copy(playsFirst = color)
  }

  case class Props(playerChoices: Vector[PlayerChoice],
                   variationChoices: Vector[Variation],
                   initialDarkPlayer: Int,
                   initialLightPlayer: Int,
                   initialPlaysFirst: Color,
                   initialVariationIndex: Int,
                   callbacks: NewGameDialogCallbacks) {
    def initialState: State = State(initialDarkPlayer, initialLightPlayer, initialPlaysFirst, initialVariationIndex)
  }


  trait PlayerSelectorCallbacks {
    def handlePlayerChanged(event: PlayerChangeEvent): Callback
  }

  trait PlayerSelectorProps {
    def color: Color
    def playerChoices: Vector[PlayerChoice]
    def playerIndex: Int
    def callbacks: PlayerSelectorCallbacks
  }

  class PlayerSelectorBackend($: BackendScope[PlayerSelectorProps, Unit]) {
    def render(props: PlayerSelectorProps) = {
      var items = new js.Array[ReactNode]
      props.playerChoices.indices.foreach { i =>
        val item = props.playerChoices(i)
        val option = <.option(
          ^.key := i,
          ^.value := i,
          item.displayName
        )
        items.push(option)
      }

      <.select(
        ^.value := props.playerIndex,
        ^.onChange ==> handleChange,
        items
      )
    }

    private def handleChange(event: ReactEventI): Callback = {
      val newValue = StringUtils.safeStringToInt(event.target.value, -1)
      if(newValue < 0) Callback.empty
      else for {
        props <- $.props
        pce = PlayerChangeEvent(props.color, newValue)
        cb <- props.callbacks.handlePlayerChanged(pce)
      } yield cb
    }
  }

  private val PlayerSelector = ReactComponentB[PlayerSelectorProps]("PlayerSelector")
    .renderBackend[PlayerSelectorBackend]
    .build


  trait PlaysFirstCallbacks {
    def handlePlaysFirstChanged(color: Color): Callback
  }

  trait PlaysFirstProps {
    def color: Color
    def playsFirst: Boolean
    def callbacks: PlaysFirstCallbacks
  }

  class PlaysFirstCheckboxBackend($: BackendScope[PlaysFirstProps, Unit]) {
    def render(props: PlaysFirstProps) = {
      <.label(
        <.input(
          ^.`type` := "checkbox",
          ^.checked := props.playsFirst,
          ^.onChange ==> handleChange
        ),
        "Plays first"
      )
    }

    private def handleChange(event: ReactEventI): Callback = {
      val checked = event.target.checked
      for {
        props <- $.props
        newColor = if(checked) props.color else OPPONENT(props.color)
        cb <- props.callbacks.handlePlaysFirstChanged(newColor)
      } yield cb
    }
  }

  private val PlaysFirstCheckbox = ReactComponentB[PlaysFirstProps]("PlaysFirstCheckbox")
    .renderBackend[PlaysFirstCheckboxBackend]
    .build

  trait PlayerPanelCallbacks extends PlayerSelectorCallbacks with PlaysFirstCallbacks

  case class PlayerSettingsPanelProps(color: Color,
                                      playerChoices: Vector[PlayerChoice],
                                      playerIndex: Int,
                                      playsFirst: Boolean,
                                      callbacks: PlayerPanelCallbacks) extends PlayerSelectorProps with PlaysFirstProps

  class PlayerSettingsPanelBackend($: BackendScope[PlayerSettingsPanelProps, Unit]) {
    def render(props: PlayerSettingsPanelProps) = {
      val playerSelector = PlayerSelector(props)
      val playsFirst = PlaysFirstCheckbox(props)

      <.div(
        playerSelector,
        playsFirst
      )
    }
  }

  private val PlayerSettingsPanel = ReactComponentB[PlayerSettingsPanelProps]("PlayerSettingsPanel")
    .renderBackend[PlayerSettingsPanelBackend]
    .build


  trait VariationSelectorCallbacks {
    def handleVariationChanged(event: VariationChangeEvent): Callback
  }

  trait VariationSelectorProps {
    def variationChoices: Vector[Variation]
    def variationIndex: Int
    def callbacks: VariationSelectorCallbacks
  }

  class VariationSelectorBackend($: BackendScope[VariationSelectorProps, Unit]) {
    def render(props: VariationSelectorProps) = {
      var items = new js.Array[ReactNode]
      props.variationChoices.indices.foreach { i =>
        val item = props.variationChoices(i)
        val option = <.option(
          ^.key := i,
          ^.value := i,
          item.displayName
        )
        items.push(option)
      }

      <.select(
        ^.value := props.variationIndex,
        ^.onChange ==> handleChange,
        items
      )
    }

    private def handleChange(event: ReactEventI): Callback = {
      val newValue = StringUtils.safeStringToInt(event.target.value, -1)
      if(newValue < 0) Callback.empty
      else for {
        props <- $.props
        vce = VariationChangeEvent(newValue)
        cb <- props.callbacks.handleVariationChanged(vce)
      } yield cb
    }
  }

  private val VariationSelector = ReactComponentB[VariationSelectorProps]("VariationSelector")
    .renderBackend[VariationSelectorBackend]
    .build

  trait GeneralSettingsPanelCallbacks extends VariationSelectorCallbacks

  case class GeneralSettingsPanelProps(variationChoices: Vector[Variation],
                                       variationIndex: Int,
                                       callbacks: GeneralSettingsPanelCallbacks) extends VariationSelectorProps

  class GeneralSettingsPanelBackend($: BackendScope[GeneralSettingsPanelProps, Unit]) {
    def render(props: GeneralSettingsPanelProps) = {
      val variationSelector = VariationSelector(props)
      <.div(
        variationSelector
      )
    }
  }

  private val GeneralSettingsPanel = ReactComponentB[GeneralSettingsPanelProps]("GeneralSettingsPanel")
    .renderBackend[GeneralSettingsPanelBackend]
    .build


  trait DialogButtonsCallbacks {
    def onOkClicked: Callback

    def onCancelClicked: Callback
  }

  class DialogButtonsBackend($: BackendScope[DialogButtonsCallbacks, Unit]) {
    def render(props: DialogButtonsCallbacks) = {
      <.div(
        <.button(
          ^.onClick --> props.onOkClicked,
          "OK"
        ),
        <.button(
          ^.onClick --> props.onCancelClicked,
          "Cancel"
        )
      )
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
          ^.`class` := "modal-background",
          <.div(
            ^.`class` := "modal-content",
            <.div(
              ^.`class` := "modal-header",
              <.h2("New Game")
            ),
            <.div(
              ^.`class` := "modal-body",
              darkPlayerPanel,
              lightPlayerPanel,
              generalSettingsPanel
            ),
            <.div(
              ^.`class` := "modal-footer",
              dialogButtons
            )
          )
        )
      )
    }

    def handlePlayerChanged(event: PlayerChangeEvent) = $.modState(_.withPlayerChange(event))

    def handlePlaysFirstChanged(color: Color) = $.modState(_.withPlaysFirst(color))

    def handleVariationChanged(event: VariationChangeEvent) = $.modState(_.withVariationChange(event))

    def onOkClicked: Callback = for {
      props <- $.props
      state <- $.state
      data = Ok(playerChoices = props.playerChoices,
        variationChoices = props.variationChoices,
        darkPlayerIndex = state.darkPlayerIndex,
        lightPlayerIndex = state.lightPlayerIndex,
        playsFirst = state.playsFirst,
        variationIndex = state.variationIndex)
      result <- props.callbacks.onNewGameDialogResult(data)
    } yield result

    def onCancelClicked: Callback = for {
      props <- $.props
      data = Cancel
      result <- props.callbacks.onNewGameDialogResult(data)
    } yield result


  }

  val component = ReactComponentB[Props]("NewGameDialog")
    .initialState_P[State](_.initialState)
    .renderBackend[NewGameDialogBackend]
    .build

  def apply(props: Props) = component(props)

}