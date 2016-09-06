package checkers.components.chrome

import checkers.core.{GameModelReader, SideChromeLayoutSettings}
import checkers.style.GlobalStyles
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactAttr
import japgolly.scalajs.react.vdom.prefix_<^._

object SideChrome {

  case class Props(gameModel: GameModelReader,
                   layoutSettings: SideChromeLayoutSettings)

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
      val layoutSettings = props.layoutSettings
      val widthPixels = layoutSettings.SideChromeWidthPixels
      val heightPixels = layoutSettings.GameSceneHeightPixels
      val buttonX = layoutSettings.SideChromeButtonPaddingPixelsX
      val buttonY = layoutSettings.SideChromeButtonAreaPaddingY
      val buttonWidth = widthPixels - (2 * buttonX)
      val buttonHeight = layoutSettings.SideChromeButtonHeightPixels

      val buttonCenterX = buttonX + (buttonWidth / 2)
      val buttonCenterY = buttonY + (buttonHeight / 2)

      val newGameButton = Button(Button.Props(buttonCenterX,
        buttonCenterY,
        buttonWidth,
        buttonHeight,
        "New Game",
        Some("Start a new game")))
      <.svg.svg(
        ^.`class` := "side-chrome",
        Backdrop((widthPixels, heightPixels)),
        newGameButton
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