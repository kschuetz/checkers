package checkers.userinterface

import checkers.userinterface.chrome.{GameOverPanel, SideChrome, TopChrome}
import checkers.core.{ApplicationCallbacks, GameModelReader, ScreenLayoutSettings}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object GameScreen {

  type Callbacks = BoardCallbacks

  case class Props(gameModel: GameModelReader,
                   screenLayoutSettings: ScreenLayoutSettings,
                   callbacks: Callbacks,
                   applicationCallbacks: ApplicationCallbacks)

}

class GameScreen(sceneContainer: SceneContainer,
                 topChrome: TopChrome,
                 sideChrome: SideChrome,
                 gameOverPanel: GameOverPanel) {
  import GameScreen._

  val create = ReactComponentB[Props]("GameScreen")
    .render_P { case Props(gameModel, layoutSettings, callbacks, applicationCallbacks) =>
      val sceneWidth = layoutSettings.GameSceneWidthPixels
      val sceneHeight = layoutSettings.GameSceneHeightPixels
      val topChromeProps = TopChrome.Props(gameModel, sceneWidth,
        layoutSettings.TopChromeHeightPixels, applicationCallbacks)
      val sideChromeProps = SideChrome.Props(gameModel,  layoutSettings, applicationCallbacks)

      val gameSceneY = layoutSettings.TopChromeHeightPixels + layoutSettings.TopChromePaddingPixels
      val sideChromeX = sceneWidth + layoutSettings.SideChromePaddingPixels

      val sceneContainerTransform = s"translate(0,$gameSceneY)"

      val sideChromeTransform = s"translate($sideChromeX,$gameSceneY)"

      val totalWidth = sideChromeX + layoutSettings.SideChromeWidthPixels
      val totalHeight = gameSceneY + sceneHeight

      val sceneContainerProps = SceneContainer.Props(gameModel, layoutSettings, callbacks)

      val gameOverPanelElement = gameModel.gameOverState.map { gameOverState =>
        val width = layoutSettings.GameOverPanelWidthPixels
        val height = layoutSettings.GameOverPanelHeightPixels
        val props = GameOverPanel.Props(widthPixels = width,
          heightPixels = height, gameOverState = gameOverState,
          applicationCallbacks = applicationCallbacks)
        val panel = gameOverPanel.create(props)
        val translateX = (sceneWidth - width) / 2
        val translateY = gameSceneY + (sceneHeight - height) / 2
        val transform = s"translate($translateX,$translateY)"
        <.svg.g(
          ^.svg.transform := transform,
          panel
        )
      }

      val topChromeElement = topChrome.create(topChromeProps)
      val sideChromeElement = sideChrome.create(sideChromeProps)
      val sceneContainerElement = sceneContainer.create(sceneContainerProps)

      <.svg.svg(
        ^.id := "game-screen",
        ^.svg.width := s"${totalWidth}px",
        ^.svg.height := s"${totalHeight}px",
        <.svg.g(
          topChromeElement
        ),
        <.svg.g(
          sceneContainerElement,
          ^.svg.transform := sceneContainerTransform
        ),
        <.svg.g(
          sideChromeElement,
          ^.svg.transform := sideChromeTransform
        ),
        gameOverPanelElement
      )

    }.build

}