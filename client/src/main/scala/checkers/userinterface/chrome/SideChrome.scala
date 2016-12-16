package checkers.userinterface.chrome

import checkers.consts._
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
                 powerMeter: PowerMeter,
                 drawCountdownIndicator: DrawCountdownIndicator,
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
      val gameModel = props.gameModel
      val layoutSettings = props.layoutSettings
      val widthPixels = layoutSettings.SideChromeWidthPixels
      val heightPixels = layoutSettings.GameSceneHeightPixels
      val halfWidth = widthPixels / 2
      val buttonX = layoutSettings.SideChromeButtonPaddingPixelsX
      val buttonY = layoutSettings.SideChromeButtonAreaPaddingY
      val buttonWidth = widthPixels - (2 * buttonX)
      val buttonHeight = layoutSettings.SideChromeButtonHeightPixels
      val paddingY = layoutSettings.SideChromeButtonPaddingPixelsY
      val buttonYSpacing = buttonHeight + paddingY
      val logEntryHeightPixels = layoutSettings.GameLogEntryHeightPixels

      val parts = new js.Array[ReactNode]

      val buttonCenterX = buttonX + (buttonWidth / 2)
      var currentY = buttonY + (buttonHeight / 2)

      val newGameButton = button.create.withKey("new-game-button")(Button.Props(buttonCenterX,
        currentY,
        buttonWidth,
        buttonHeight,
        caption = "New Game...",
        tooltip = Some("Start a new game"),
        onClick = props.applicationCallbacks.onNewGameButtonClicked))

      parts.push(newGameButton)

      currentY += buttonYSpacing

      val rotateBoardButton = button.create.withKey("rotate-button")(Button.Props(buttonCenterX,
        currentY,
        buttonWidth,
        buttonHeight,
        caption = "Rotate",
        tooltip = Some("Rotate the view of the board 180 degrees"),
        onClick = props.applicationCallbacks.onRotateBoardButtonClicked))

      parts.push(rotateBoardButton)

      currentY += buttonYSpacing

      if(gameModel.hintButtonEnabled) {
        val hintButton = button.create.withKey("hint-button")(Button.Props(buttonCenterX,
          currentY,
          buttonWidth,
          buttonHeight,
          caption = "Hint",
          onClick = props.applicationCallbacks.onHintButtonClicked))

        parts.push(hintButton)
      }

      currentY += buttonYSpacing

      val powerMeterHeight = layoutSettings.SideChromePowerMeterHeightPixels

      val powerMeterElement = {
        val darkScore = props.gameModel.getScore(DARK)
        val lightScore = props.gameModel.getScore(LIGHT)

        val tooltip = if(darkScore > lightScore)
          s"Dark advantage ${(darkScore - lightScore) / 2}"
        else if (lightScore > darkScore)
          s"Light advantage ${(lightScore - darkScore) / 2}"
        else "Dark and light equal advantage"

        val position = PowerMeter.getPosition(darkScore, lightScore)

        val powerMeterProps = PowerMeter.Props(
          centerX = halfWidth,
          centerY = currentY + (0.5 * powerMeterHeight),
          widthPixels = layoutSettings.SideChromePowerMeterWidthPixels,
          heightPixels = powerMeterHeight,
          position = position,
          tooltip = Some(tooltip))

        powerMeter.create.withKey("power-meter")(powerMeterProps)
      }

      parts.push(powerMeterElement)

      currentY += powerMeterHeight + paddingY

      val drawCountdownIndicatorHeight = layoutSettings.SideChromeDrawCountdownIndicatorHeightPixels

      val movesUntilDraw = Some(99)

      movesUntilDraw.foreach { value =>
        val drawCountdownProps = DrawCountdownIndicator.Props(
          centerX = halfWidth,
          centerY = currentY + (0.5 * drawCountdownIndicatorHeight),
          widthPixels = layoutSettings.SideChromeDrawCountdownIndicatorWidthPixels,
          heightPixels = drawCountdownIndicatorHeight,
          movesUntilDraw = value
        )

        val element = drawCountdownIndicator.create.withKey("draw-countdown")(drawCountdownProps)
        parts.push(element)
      }

      currentY += drawCountdownIndicatorHeight + paddingY

      val gameLogLeft = layoutSettings.GameLogPaddingPixelsX
      val gameLogWidth = widthPixels - (2 * gameLogLeft)
      val gameLogTop = currentY
      val gameLogBottom = heightPixels - layoutSettings.GameLogPaddingPixelsY
      val gameLogHeight = gameLogBottom - gameLogTop
      if(gameLogHeight > 0) {
        val gameLogProps = GameLogDisplay.Props(gameLogLeft, gameLogTop, gameLogWidth, gameLogHeight,
          logEntryHeightPixels, layoutSettings.GameLogScrollButtonHeightPixels, gameModel.gameLogUpdateId,
          gameModel.inputPhase.waitingForMove, gameModel.currentTurnSnapshot, gameModel.history)
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