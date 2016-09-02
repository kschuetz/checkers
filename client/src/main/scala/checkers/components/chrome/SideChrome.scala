package checkers.components.chrome

import checkers.core.GameModelReader
import checkers.style.GlobalStyles
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactAttr
import japgolly.scalajs.react.vdom.prefix_<^._

object SideChrome {

  case class Props(gameModel: GameModelReader,
                   widthPixels: Int,
                   heightPixels: Int)

  private val Backdrop = ReactComponentB[(Int, Int)]("SideChromeBackdrop")
    .render_P { case (width, height) =>
      <.svg.rect(
        ReactAttr.ClassName := "side-chrome-backdrop",
        ^.svg.x := 0,
        ^.svg.y := 0,
        ^.svg.width := width,
        ^.svg.height := height
      )
    }
    .build

  class SideChromeBackend($: BackendScope[Props, Unit]) {

    def render(props: Props) = {
      <.svg.svg(
        ^.`class` := "side-chrome",
        Backdrop((props.widthPixels, props.heightPixels))
      )
    }

//    def render(props: Props) = {
//      <.div(
//        ^.`class` := "side-chrome",
//        <.div(
//          ^.`class` := "row",
//          Bootstrap.Button(Bootstrap.Button.Props(onNewGameClick), "New Game"),
//          <.button(^.`class` := "btn default",
//            ^.tpe := "button",
//            ^.title := "Rotate the board 180 degrees",
//            ^.onClick --> onRotateBoardClick,
//            "Rotate board"
//          )
//        )
//
//      )
//    }

    private def onNewGameClick = Callback {
      println("new game click")
    }

    private def onRotateBoardClick = Callback {
      println("rotate board click")
    }
  }

  val component = ReactComponentB[Props]("SideChrome")
    .renderBackend[SideChromeBackend]
    //    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, _) =>
    //      val result = scope.props != nextProps
    //      CallbackTo.pure(result)
    //    }
    .build

  def apply(props: Props) = component(props)

  private def bss = GlobalStyles.bootstrapStyles


}