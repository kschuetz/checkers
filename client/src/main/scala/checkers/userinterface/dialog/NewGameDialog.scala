package checkers.userinterface.dialog

import checkers.userinterface.piece.{PhysicalPiece, PhysicalPieceProps}
import checkers.consts._
import checkers.core.Variation
import checkers.util.StringUtils
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js


object NewGameDialog {

  sealed trait Result

  case object Cancel extends Result

  case class Ok(playerChoices: Vector[PlayerChoice],
                variationChoices: Vector[Variation],
                darkPlayerIndex: Int,
                lightPlayerIndex: Int,
                playsFirst: Side,
                variationIndex: Int) extends Result

  trait NewGameDialogCallbacks {
    def onNewGameDialogResult(result: Result): Callback
  }

  case class PlayerChangeEvent(side: Side,
                               playerIndex: Int)

  case class VariationChangeEvent(variationIndex: Int)

  case class State(darkPlayerIndex: Int,
                   lightPlayerIndex: Int,
                   playsFirst: Side,
                   variationIndex: Int) {
    def withPlayerChange(event: PlayerChangeEvent): State =
      if (event.side == DARK) {
        copy(darkPlayerIndex = event.playerIndex)
      } else {
        copy(lightPlayerIndex = event.playerIndex)
      }

    def withVariationChange(event: VariationChangeEvent): State =
      copy(variationIndex = event.variationIndex)

    def withPlaysFirst(side: Side): State =
      copy(playsFirst = side)
  }

  case class Props(playerChoices: Vector[PlayerChoice],
                   variationChoices: Vector[Variation],
                   initialDarkPlayer: Int,
                   initialLightPlayer: Int,
                   initialPlaysFirst: Side,
                   initialVariationIndex: Int,
                   callbacks: NewGameDialogCallbacks) {
    def initialState: State = State(initialDarkPlayer, initialLightPlayer, initialPlaysFirst, initialVariationIndex)
  }

}

class NewGameDialog(physicalPiece: PhysicalPiece) {

  import NewGameDialog._

  private val PieceAvatar = ReactComponentB[Side]("NewGameDialogPieceAvatar")
    .render_P { side =>
      val pieceProps = PhysicalPieceProps.default.copy(
        piece = if (side == DARK) DARKMAN else LIGHTMAN,
        x = 45,
        y = 45,
        scale = 90
      )
      val component = physicalPiece.component(pieceProps)
      <.svg.svg(
        ^.svg.width := 90,
        ^.svg.height := 90,
        component
      )
    }
    .build

  trait PlayerSelectorCallbacks {
    def handlePlayerChanged(event: PlayerChangeEvent): Callback
  }

  trait PlayerSelectorProps {
    def side: Side

    def playerChoices: Vector[PlayerChoice]

    def playerIndex: Int

    def callbacks: PlayerSelectorCallbacks
  }

  class PlayerSelectorBackend($: BackendScope[PlayerSelectorProps, Unit]) {
    def render(props: PlayerSelectorProps) = {
      val items = new js.Array[ReactNode]
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
        ^.`class` := "player-selector",
        ^.value := props.playerIndex,
        ^.onChange ==> handleChange,
        items
      )
    }

    private def handleChange(event: ReactEventI): Callback = {
      val newValue = StringUtils.safeStringToInt(event.target.value, -1)
      if (newValue < 0) Callback.empty
      else for {
        props <- $.props
        pce = PlayerChangeEvent(props.side, newValue)
        cb <- props.callbacks.handlePlayerChanged(pce)
      } yield cb
    }
  }

  private val PlayerSelector = ReactComponentB[PlayerSelectorProps]("PlayerSelector")
    .renderBackend[PlayerSelectorBackend]
    .build


  trait PlaysFirstCallbacks {
    def handlePlaysFirstChanged(side: Side): Callback
  }

  trait PlaysFirstProps {
    def side: Side

    def playsFirst: Boolean

    def callbacks: PlaysFirstCallbacks
  }

  class PlaysFirstCheckboxBackend($: BackendScope[PlaysFirstProps, Unit]) {
    def render(props: PlaysFirstProps) = {
      <.div(
        ^.`class` := "plays-first",
        <.label(
          <.input(
            ^.`type` := "checkbox",
            ^.checked := props.playsFirst,
            ^.onChange ==> handleChange
          ),
          "Plays first"
        )
      )
    }

    private def handleChange(event: ReactEventI): Callback = {
      val checked = event.target.checked
      for {
        props <- $.props
        newSide = if (checked) props.side else OPPONENT(props.side)
        cb <- props.callbacks.handlePlaysFirstChanged(newSide)
      } yield cb
    }
  }

  private val PlaysFirstCheckbox = ReactComponentB[PlaysFirstProps]("PlaysFirstCheckbox")
    .renderBackend[PlaysFirstCheckboxBackend]
    .build

  trait PlayerPanelCallbacks extends PlayerSelectorCallbacks with PlaysFirstCallbacks

  case class PlayerSettingsPanelProps(side: Side,
                                      playerChoices: Vector[PlayerChoice],
                                      playerIndex: Int,
                                      playsFirst: Boolean,
                                      callbacks: PlayerPanelCallbacks) extends PlayerSelectorProps with PlaysFirstProps

  class PlayerSettingsPanelBackend($: BackendScope[PlayerSettingsPanelProps, Unit]) {
    def render(props: PlayerSettingsPanelProps) = {
      val avatar = PieceAvatar(props.side)
      val playerSelector = PlayerSelector(props)
      val playsFirst = PlaysFirstCheckbox(props)

      <.div(
        avatar,
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
      val items = new js.Array[ReactNode]
      props.variationChoices.indices.foreach { i =>
        val item = props.variationChoices(i)
        val option = <.option(
          ^.key := i,
          ^.value := i,
          item.displayName
        )
        items.push(option)
      }

      <.div(
        ^.`class` := "variation-selector",
        <.label(
          ^.`for` := "variation-select",
          "Variation"
        ),
        <.select(
          ^.id := "variation-select",
          ^.value := props.variationIndex,
          ^.onChange ==> handleChange,
          items
        )
      )

    }

    private def handleChange(event: ReactEventI): Callback = {
      val newValue = StringUtils.safeStringToInt(event.target.value, -1)
      if (newValue < 0) Callback.empty
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
        ^.`class` := "general-settings",
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
      val darkPlayerProps = PlayerSettingsPanelProps(side = DARK,
        playerChoices = props.playerChoices,
        playerIndex = state.darkPlayerIndex,
        playsFirst = state.playsFirst == DARK,
        callbacks = this)

      val darkPlayerPanel = PlayerSettingsPanel(darkPlayerProps)

      val lightPlayerProps = darkPlayerProps.copy(
        side = LIGHT,
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
              <.span(
                ^.`class` := "close",
                ^.onClick --> onCancelClicked,
                "×"
              ),
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

    def handlePlayerChanged(event: PlayerChangeEvent): Callback = $.modState(_.withPlayerChange(event))

    def handlePlaysFirstChanged(side: Side): Callback = $.modState(_.withPlaysFirst(side))

    def handleVariationChanged(event: VariationChangeEvent): Callback = $.modState(_.withVariationChange(event))

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

}