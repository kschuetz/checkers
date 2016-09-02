package checkers.components

import checkers.components.chrome.TopChrome
import checkers.core.{GameModelReader, ScreenLayoutSettings}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object GameScreen {

  type Callbacks = BoardCallbacks

  case class Props(gameModel: GameModelReader,
                   screenLayoutSettings: ScreenLayoutSettings,
                   callbacks: Callbacks)

  val component = ReactComponentB[Props]("GameScreen")
    .render_P { case Props(gameModel, screenLayoutSettings, callbacks) =>
      val topChromeProps = TopChrome.Props(gameModel, screenLayoutSettings.GameSceneWidthPixels,
        screenLayoutSettings.TopChromeHeightPixels)
      //      val sideChromeProps = SideChrome.Props(props._1)

      val gameSceneY = screenLayoutSettings.TopChromeHeightPixels + screenLayoutSettings.TopChromePaddingPixels
      val sideChromeX = screenLayoutSettings.GameSceneWidthPixels + screenLayoutSettings.SideChromePaddingPixels

      val sceneContainerTransform = s"translate(0,$gameSceneY)"

      val sideChromeTransform = s"translate($sideChromeX,$gameSceneY)"

      val totalWidth = sideChromeX + screenLayoutSettings.SideChromeWidthPixels
      val totalHeight = gameSceneY + screenLayoutSettings.GameSceneHeightPixels

      val sceneContainerProps = SceneContainer.Props(gameModel, screenLayoutSettings, callbacks)

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
        )
      )

    }.build

  val apply = component

}