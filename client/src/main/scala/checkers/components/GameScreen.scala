package checkers.components

import checkers.components.chrome.TopChrome
import checkers.models.GameModelReader
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object GameScreen {

  type Callbacks = BoardCallbacks

  type Props = (GameModelReader, Callbacks)

  val component = ReactComponentB[Props]("GameScreen")
    .render_P { props =>
      val topChromeProps = TopChrome.Props(props._1, 800, 90)

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
          <.div(
            ^.`class` := "col-md-12",
            SceneContainer(props)
          )
        )
      )

    }.build

  val apply = component

}