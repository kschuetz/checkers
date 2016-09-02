package checkers.components

import checkers.components.chrome.{SideChrome, TopChrome}
import checkers.core.{GameModelReader, ScreenLayoutSettings}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.SVGSVGElement

object GameScreen {

  type Callbacks = BoardCallbacks

  case class Props(gameModel: GameModelReader,
                   screenLayoutSettings: ScreenLayoutSettings,
                   callbacks: Callbacks)

  val component = ReactComponentB[Props]("GameScreen")
    .render_P { case Props(gameModel, screenLayoutSettings, callbacks) =>
      val topChromeProps = TopChrome.Props(gameModel, 800, 90)
//      val sideChromeProps = SideChrome.Props(props._1)

      val sceneContainerProps = SceneContainer.Props(gameModel, screenLayoutSettings, callbacks)

      <.div(
        ^.id := "game-screen",
        <.div(
          ^.`class` := "row",
          <.div(
            ^.`class` := "col-md-12",
            TopChrome(topChromeProps)
          )
        ),
        <.div(
          ^.`class` := "row",
          <.svg.svg(
            ^.svg.width := "1100px",
            ^.svg.height := "800px",
            SceneContainer(sceneContainerProps)
          )
//          <.div(
//            ^.`class` := "col-md-11",
//            SceneContainer(props)
//          ),
//          <.div(
//            ^.`class` := "col-md-1"
//            //SideChrome(sideChromeProps)
//          )
        )
      )

    }.build

  val apply = component

}