package checkers.components

import checkers.models.GameModelReader
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object GameScreen {

  type Callbacks = PieceCallbacks with BoardCallbacks

  type Props = (GameModelReader, Callbacks)

  val component = ReactComponentB[Props]("GameScreen")
    .render_P { case props =>
      <.div(
        ^.id := "game-screen",
        <.div(
          ^.`class` := "row"
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