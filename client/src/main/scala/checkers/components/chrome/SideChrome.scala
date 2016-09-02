package checkers.components.chrome

import checkers.components.Bootstrap
import checkers.components.Bootstrap.CommonStyle
import checkers.consts._
import checkers.core.{GameModelReader, PlayerDescription}
import checkers.style.GlobalStyles
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object SideChrome {

  case class Props(gameModel: GameModelReader,
                   widthPixels: Int,
                   heightPixels: Int)

  class SideChromeBackend($: BackendScope[Props, Unit]) {

    def render(props: Props) = {
      <.div(
        ^.`class` := "side-chrome",
        <.div(
          ^.`class` := "row",
          Bootstrap.Button(Bootstrap.Button.Props(onNewGameClick), "New Game"),
          <.button(^.`class` := "btn default",
            ^.tpe := "button",
            ^.title := "Rotate the board 180 degrees",
            ^.onClick --> onRotateBoardClick,
            "Rotate board"
          )
        )

      )
    }

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