package checkers.userinterface.chrome

import checkers.consts._
import checkers.core.{ApplicationCallbacks, GameModelReader, SideChromeLayoutSettings}
import checkers.userinterface.gamelog.GameLogDisplay
import checkers.userinterface.widgets.Button
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{svg_<^ => svg}

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

  private val Backdrop = ScalaComponent.build[(Int, Int)]("SideChromeBackdrop")
    .render_P { case (width, height) =>
      svg.<.rect(
//        VdomAttr.ClassName := "side-chrome-backdrop",
        ^.`class` := "side-chrome-backdrop",
        svg.^.x := 0.asInstanceOf[JsNumber],
        svg.^.y := 0.asInstanceOf[JsNumber],
        svg.^.width := width.asInstanceOf[JsNumber],
        svg.^.height := height.asInstanceOf[JsNumber]
      )
    }
    .build

  class Backend($: BackendScope[Props, Unit]) {

    def render(props: Props): VdomElement = {
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

      val buttonRoundness = 0.2 * math.min(buttonWidth, buttonHeight)

      val parts = VdomArray.empty

      val buttonCenterX = buttonX + (buttonWidth / 2)
      var currentY = buttonY + (buttonHeight / 2)

      val newGameButton = button.create.withKey("new-game-button")(Button.Props(buttonCenterX,
        currentY,
        buttonWidth,
        buttonHeight,
        radiusX = buttonRoundness,
        radiusY = buttonRoundness,
        caption = "New Game...",
        tooltip = Some("Start a new game"),
        onClick = props.applicationCallbacks.onNewGameButtonClicked))(VdomArray.empty)

      parts += newGameButton

      currentY += buttonYSpacing

      val rotateBoardButton = button.create.withKey("rotate-button")(Button.Props(buttonCenterX,
        currentY,
        buttonWidth,
        buttonHeight,
        radiusX = buttonRoundness,
        radiusY = buttonRoundness,
        caption = "Rotate",
        tooltip = Some("Rotate the view of the board 180 degrees"),
        onClick = props.applicationCallbacks.onRotateBoardButtonClicked))(VdomArray.empty)

      parts += rotateBoardButton

      currentY += buttonYSpacing

      if(gameModel.hintButtonEnabled) {
        val hintButton = button.create.withKey("hint-button")(Button.Props(buttonCenterX,
          currentY,
          buttonWidth,
          buttonHeight,
          radiusX = buttonRoundness,
          radiusY = buttonRoundness,
          caption = "Hint",
          onClick = props.applicationCallbacks.onHintButtonClicked))(VdomArray.empty)

        parts += hintButton
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

      parts += powerMeterElement

      currentY += powerMeterHeight + paddingY

      val drawCountdownIndicatorHeight = layoutSettings.SideChromeDrawCountdownIndicatorHeightPixels

      val movesUntilDraw = gameModel.turnsRemainingUntilDrawHint

      movesUntilDraw.foreach { value =>
        val drawCountdownProps = DrawCountdownIndicator.Props(
          centerX = halfWidth,
          centerY = currentY + (0.5 * drawCountdownIndicatorHeight),
          widthPixels = layoutSettings.SideChromeDrawCountdownIndicatorWidthPixels,
          heightPixels = drawCountdownIndicatorHeight,
          movesUntilDraw = value
        )

        val element = drawCountdownIndicator.create.withKey("draw-countdown")(drawCountdownProps)
        parts += element
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
        parts += element
      }

      val backdrop = Backdrop((widthPixels, heightPixels))

      svg.<.svg(
        ^.`class` := "side-chrome",
        backdrop,
        parts
      )

    }

  }

  val create = ScalaComponent.build[Props]("SideChrome")
    .renderBackend[Backend]
    //    .shouldComponentUpdateConst { case ShouldComponentUpdate(scope, nextProps, _) =>
    //      val result = scope.props != nextProps
    //      CallbackTo.pure(result)
    //    }
    .build

}