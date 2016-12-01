package checkers.components.chrome

import checkers.core.{ApplicationCallbacks, GameModelReader, SideChromeLayoutSettings}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactAttr
import japgolly.scalajs.react.vdom.prefix_<^._

object SideChrome {

  case class Props(gameModel: GameModelReader,
                   layoutSettings: SideChromeLayoutSettings,
                   applicationCallbacks: ApplicationCallbacks)

  private val Backdrop = ReactComponentB[(Int, Int)]("SideChromeBackdrop")
    .render_P { case (width, height) =>
      <.svg.rect(
        ReactAttr.ClassName := "side-chrome-backdrop",
        ^.svg.x := 0,
        ^.svg.y := 0,
        ^.svg.width := width,
        ^.svg.height := height
      )
    }
    .build

  class SideChromeBackend($: BackendScope[Props, Unit]) {

    def render(props: Props) = {
      val layoutSettings = props.layoutSettings
      val widthPixels = layoutSettings.SideChromeWidthPixels
      val heightPixels = layoutSettings.GameSceneHeightPixels
      val buttonX = layoutSettings.SideChromeButtonPaddingPixelsX
      val buttonY = layoutSettings.SideChromeButtonAreaPaddingY
      val buttonWidth = widthPixels - (2 * buttonX)
      val buttonHeight = layoutSettings.SideChromeButtonHeightPixels
      val buttonYSpacing = buttonHeight + layoutSettings.SideChromeButtonPaddingPixelsY

      val buttonCenterX = buttonX + (buttonWidth / 2)
      var currentY = buttonY + (buttonHeight / 2)

      val newGameButton = Button(Button.Props(buttonCenterX,
        currentY,
        buttonWidth,
        buttonHeight,
        "New Game...",
        Some("Start a new game"),
        enabled = true,
        Map.empty,
        props.applicationCallbacks.onNewGameButtonClicked))

      currentY += buttonYSpacing

      val rotateBoardButton = Button(Button.Props(buttonCenterX,
        currentY,
        buttonWidth,
        buttonHeight,
        "Rotate",
        Some("Rotate the view of the board 180 degrees"),
        enabled = true,
        Map.empty,
        props.applicationCallbacks.onRotateBoardButtonClicked))

      <.svg.svg(
        ^.`class` := "side-chrome",
        Backdrop((widthPixels, heightPixels)),
        newGameButton,
        rotateBoardButton
      )
    }

  }

  val component = ReactComponentB[Props]("SideChrome")
    .renderBackend[SideChromeBackend]
    //    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, _) =>
    //      val result = scope.props != nextProps
    //      CallbackTo.pure(result)
    //    }
    .build

  def apply(props: Props) = component(props)


}