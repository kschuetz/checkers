package checkers.components

import checkers.components.chrome.{SideChrome, TopChrome}
import checkers.models.GameModelReader
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.SVGSVGElement

object GameScreen {

  type Callbacks = BoardCallbacks

  type Props = (GameModelReader, Callbacks)

  val component = ReactComponentB[Props]("GameScreen")
    .render_P { props =>
      val topChromeProps = TopChrome.Props(props._1, 800, 90)
//      val sideChromeProps = SideChrome.Props(props._1)

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
            SceneContainer(props)
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