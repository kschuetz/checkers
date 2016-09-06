package checkers.components

import checkers.components.chrome.{SideChrome, TopChrome}
import checkers.core.{GameModelReader, ScreenLayoutSettings}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object GameScreen {

  type Callbacks = BoardCallbacks

  case class Props(gameModel: GameModelReader,
                   screenLayoutSettings: ScreenLayoutSettings,
                   callbacks: Callbacks)

  val component = ReactComponentB[Props]("GameScreen")
    .render_P { case Props(gameModel, layoutSettings, callbacks) =>
      val topChromeProps = TopChrome.Props(gameModel, layoutSettings.GameSceneWidthPixels,
        layoutSettings.TopChromeHeightPixels)
      val sideChromeProps = SideChrome.Props(gameModel,  layoutSettings)

      val gameSceneY = layoutSettings.TopChromeHeightPixels + layoutSettings.TopChromePaddingPixels
      val sideChromeX = layoutSettings.GameSceneWidthPixels + layoutSettings.SideChromePaddingPixels

      val sceneContainerTransform = s"translate(0,$gameSceneY)"

      val sideChromeTransform = s"translate($sideChromeX,$gameSceneY)"

      val totalWidth = sideChromeX + layoutSettings.SideChromeWidthPixels
      val totalHeight = gameSceneY + layoutSettings.GameSceneHeightPixels

      val sceneContainerProps = SceneContainer.Props(gameModel, layoutSettings, callbacks)

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
        )
      )

    }.build

  val apply = component

}