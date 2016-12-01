package checkers.components.chrome

import checkers.util.SvgHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object RushButton extends SvgHelpers {

  private lazy val tooltip = Some("Rush the computer into making a move")

  case class Props(centerX: Double,
                   centerY: Double,
                   width: Double,
                   height: Double,
                   onClick: Callback = Callback.empty)

  class RushButtonBackend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {

      val buttonProps = Button.Props(
        centerX = props.centerX,
        centerY = props.centerY,
        width = props.width,
        height = props.height,
        caption = "",
        tooltip = tooltip,
        enabled = true,
        onClick =  props.onClick
      )

      Button(buttonProps)
    }
  }

  val component = ReactComponentB[Props]("RushButton")
    .renderBackend[RushButtonBackend]
    .build

  def apply(props: Props) = component(props)

}