package checkers.userinterface.chrome

import checkers.core.{ApplicationCallbacks, GameModelReader, SideChromeLayoutSettings}
import checkers.userinterface.gamelog.GameLogDisplay
import checkers.userinterface.widgets.Button
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactAttr
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object SideChrome {

  case class Props(gameModel: GameModelReader,
                   layoutSettings: SideChromeLayoutSettings,
                   applicationCallbacks: ApplicationCallbacks)

}

class SideChrome(button: Button,
                 gameLogDisplay: GameLogDisplay) {

  import SideChrome._

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

  class Backend($: BackendScope[Props, Unit]) {

    def render(props: Props): ReactElement = {
      val layoutSettings = props.layoutSettings
      val widthPixels = layoutSettings.SideChromeWidthPixels
      val heightPixels = layoutSettings.GameSceneHeightPixels
      val buttonX = layoutSettings.SideChromeButtonPaddingPixelsX
      val buttonY = layoutSettings.SideChromeButtonAreaPaddingY
      val buttonWidth = widthPixels - (2 * buttonX)
      val buttonHeight = layoutSettings.SideChromeButtonHeightPixels
      val buttonYSpacing = buttonHeight + layoutSettings.SideChromeButtonPaddingPixelsY
      val logEntryHeightPixels = layoutSettings.GameLogEntryHeightPixels

      val parts = new js.Array[ReactNode]

      val buttonCenterX = buttonX + (buttonWidth / 2)
      var currentY = buttonY + (buttonHeight / 2)

      val newGameButton = button.create.withKey("new-game-button")(Button.Props(buttonCenterX,
        currentY,
        buttonWidth,
        buttonHeight,
        "New Game...",
        Some("Start a new game"),
        enabled = true,
        Map.empty,
        props.applicationCallbacks.onNewGameButtonClicked))

      parts.push(newGameButton)

      currentY += buttonYSpacing

      val rotateBoardButton = button.create.withKey("rotate-button")(Button.Props(buttonCenterX,
        currentY,
        buttonWidth,
        buttonHeight,
        "Rotate",
        Some("Rotate the view of the board 180 degrees"),
        enabled = true,
        Map.empty,
        props.applicationCallbacks.onRotateBoardButtonClicked))

      parts.push(rotateBoardButton)

      currentY += buttonYSpacing

      if(props.gameModel.hintButtonEnabled) {
        val hintButton = button.create.withKey("hint-button")(Button.Props(buttonCenterX,
          currentY,
          buttonWidth,
          buttonHeight,
          "Hint",
          None,
          enabled = true,
          Map.empty,
          props.applicationCallbacks.onHintButtonClicked))

        parts.push(hintButton)
      }

      currentY += buttonYSpacing

      val gameLogLeft = layoutSettings.GameLogPaddingPixelsX
      val gameLogWidth = widthPixels - (2 * gameLogLeft)
      val gameLogTop = currentY
      val gameLogBottom = heightPixels - layoutSettings.GameLogPaddingPixelsY
      val gameLogHeight = gameLogBottom - gameLogTop
      if(gameLogHeight > 0) {
        val gameLogProps = GameLogDisplay.Props(gameLogLeft, gameLogTop, gameLogWidth, gameLogHeight,
          logEntryHeightPixels, props.gameModel)
        val element = gameLogDisplay.create.withKey("game-log")(gameLogProps)
        parts.push(element)
      }

      <.svg.svg(
        ^.`class` := "side-chrome",
        Backdrop((widthPixels, heightPixels)),
        parts
      )

    }

  }

  val create = ReactComponentB[Props]("SideChrome")
    .renderBackend[Backend]
    //    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, _) =>
    //      val result = scope.props != nextProps
    //      CallbackTo.pure(result)
    //    }
    .build

}