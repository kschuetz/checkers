package checkers.components.chrome

import checkers.components.mixins.FontHelpers
import checkers.util.SvgHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object Button extends SvgHelpers with FontHelpers {

  case class Props(centerX: Double,
                   centerY: Double,
                   width: Double,
                   height: Double,
                   caption: String = "",
                   tooltip: Option[String] = None,
                   enabled: Boolean = true,
                   extraClasses: Map[String, Boolean] = Map.empty,
                   onClick: Callback = Callback.empty)

  case class State(depressed: Boolean)

  private val defaultState = State(depressed = false)

  class ButtonBackend($: BackendScope[Props, State]) {
    def handleMouseDown(e: ReactMouseEventI) = {
      if(e.button != 0) Callback.empty  // ignore all but left-click
      else $.modState(_.copy(depressed = true))
    }

    def handleMouseUp(e: ReactMouseEventI) = {
      if(e.button != 0) Callback.empty    // ignore all but left-click
      else for {
        state <- $.state
        props <- $.props
        cb <- if(!state.depressed) Callback.empty
        else props.onClick >> $.modState(_.copy(depressed = false))
      } yield cb
    }

    def handleMouseOut(e: ReactEventI) = {
      $.modState(_.copy(depressed = false))
    }

    def render(props: Props, state: State, children: PropsChildren) = {
      val textHeight = math.round(2 * props.height / 3)
      val textY = 4 + (props.height - textHeight) / 2

      val (centerX, centerY) = if(state.depressed) {
        (props.centerX + 3, props.centerY + 3)
      } else {
        (props.centerX, props.centerY)
      }

      val caption = if(props.caption.nonEmpty) {
        Some(<.svg.text(
          ^.svg.y := textY,
          fontSize := s"${textHeight}px",
          props.caption
        ))
      } else None

      val classMap = props.extraClasses + ("enabled" -> props.enabled) + ("disabled" -> !props.enabled)

      <.svg.g(
//        ^.`class` := "button enabled",
        ^.classSet1M("button", classMap),
        ^.svg.transform := s"translate($centerX,$centerY)",
        ^.onMouseDown ==> handleMouseDown,
        ^.onMouseUp ==> handleMouseUp,
        ^.onMouseOut ==> handleMouseOut,

        <.titleTag(props.tooltip),

        <.svg.g(
          ^.`class` := "button-body",
          <.svg.rect(
            ^.svg.x := -(props.width / 2),
            ^.svg.y := -(props.height / 2),
            ^.svg.width := props.width,
            ^.svg.height := props.height,
            ^.svg.rx := 10,
            ^.svg.ry := 10
          )
        ),
        caption,
        children
      )
    }

  }

  val component = ReactComponentB[Props]("Button")
    .initialState[State](defaultState)
    .renderBackend[ButtonBackend]
    .build

  def apply(props: Props) = component(props)



}