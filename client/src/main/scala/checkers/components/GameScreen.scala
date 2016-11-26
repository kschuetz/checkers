package checkers.components

import checkers.components.chrome.{GameOverPanel, SideChrome, TopChrome}
import checkers.core.{ApplicationCallbacks, GameModelReader, ScreenLayoutSettings}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object GameScreen {

  type Callbacks = BoardCallbacks

  case class Props(gameModel: GameModelReader,
                   screenLayoutSettings: ScreenLayoutSettings,
                   callbacks: Callbacks,
                   applicationCallbacks: ApplicationCallbacks)

  val component = ReactComponentB[Props]("GameScreen")
    .render_P { case Props(gameModel, layoutSettings, callbacks, applicationCallbacks) =>
      val sceneWidth = layoutSettings.GameSceneWidthPixels
      val sceneHeight = layoutSettings.GameSceneHeightPixels
      val topChromeProps = TopChrome.Props(gameModel, sceneWidth,
        layoutSettings.TopChromeHeightPixels)
      val sideChromeProps = SideChrome.Props(gameModel,  layoutSettings, applicationCallbacks)

      val gameSceneY = layoutSettings.TopChromeHeightPixels + layoutSettings.TopChromePaddingPixels
      val sideChromeX = sceneWidth + layoutSettings.SideChromePaddingPixels

      val sceneContainerTransform = s"translate(0,$gameSceneY)"

      val sideChromeTransform = s"translate($sideChromeX,$gameSceneY)"

      val totalWidth = sideChromeX + layoutSettings.SideChromeWidthPixels
      val totalHeight = gameSceneY + sceneHeight

      val sceneContainerProps = SceneContainer.Props(gameModel, layoutSettings, callbacks)

      val gameOverPanel = gameModel.gameOverState.map { gameOverState =>
        val width = layoutSettings.GameOverPanelWidthPixels
        val height = layoutSettings.GameOverPanelHeightPixels
        val props = GameOverPanel.Props(widthPixels = width,
          heightPixels = height, gameOverState = gameOverState)
        val panel = GameOverPanel(props)
        val translateX = (sceneWidth - width) / 2
        val translateY = gameSceneY + (sceneHeight - height) / 2
        val transform = s"translate($translateX,$translateY)"
        <.svg.g(
          ^.svg.transform := transform,
          panel
        )
      }

      <.svg.svg(
        ^.id := "game-screen",
        ^.svg.width := s"${totalWidth}px",
        ^.svg.height := s"${totalHeight}px",
        <.svg.g(
          TopChrome(topChromeProps)
        ),
        <.svg.g(
          SceneContainer(sceneContainerProps),
          ^.svg.transform := sceneContainerTransform
        ),
        <.svg.g(
          SideChrome(sideChromeProps),
          ^.svg.transform := sideChromeTransform
        ),
        gameOverPanel
      )

    }.build

  val apply = component

}