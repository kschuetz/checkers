package checkers.components

import checkers.models.GameScreenModel
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object GameScreen {

  val component = ReactComponentB[GameScreenModel]("GameScreen")
    .render_P { model =>
      <.div(
        ^.id := "game-screen",
        <.div(
          ^.`class` := "row"
        ),
        <.div(
          ^.`class` := "row",
          <.div(
            ^.`class` := "col-md-12",
            SceneContainer(model)
          )
        )
      )

    }.build

  val apply = component

}