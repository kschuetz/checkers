package checkers.userinterface.widgets

import checkers.userinterface.mixins.FontHelpers
import checkers.util.SvgHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object Button {

  case class Props(centerX: Double,
                   centerY: Double,
                   width: Double,
                   height: Double,
                   radiusX: Double = 10,
                   radiusY: Double = 10,
                   caption: String = "",
                   tooltip: Option[String] = None,
                   enabled: Boolean = true,
                   extraClasses: Map[String, Boolean] = Map.empty,
                   onClick: Callback = Callback.empty)

  case class State(depressed: Boolean)

  private val defaultState = State(depressed = false)

}

class Button extends SvgHelpers with FontHelpers {
  import Button._

  class Backend($: BackendScope[Props, State]) {
    def handleMouseDown(e: ReactMouseEventI): Callback = {
      if(e.button != 0) Callback.empty  // ignore all but left-click
      else $.modState(_.copy(depressed = true))
    }

    def handleMouseUp(e: ReactMouseEventI): Callback = {
      if(e.button != 0) Callback.empty    // ignore all but left-click
      else for {
        state <- $.state
        props <- $.props
        cb <- if(!state.depressed) Callback.empty
        else props.onClick >> $.modState(_.copy(depressed = false))
      } yield cb
    }

    def handleMouseOut(e: ReactEventI): Callback = {
      $.modState(_.copy(depressed = false))
    }

    def render(props: Props, state: State, children: PropsChildren): ReactElement = {
      val textHeight = math.round(3 * props.height / 5)
      val textY = 2 + (props.height - textHeight) / 2

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
        ^.classSet1M("button", classMap),
        ^.svg.transform := s"translate($centerX,$centerY)",
        props.enabled ?= (^.onMouseDown ==> handleMouseDown),
        props.enabled ?= (^.onMouseUp ==> handleMouseUp),
        props.enabled ?= (^.onMouseOut ==> handleMouseOut),

        <.titleTag(props.tooltip),

        <.svg.g(
          ^.`class` := "button-body",
          <.svg.rect(
            ^.svg.x := -(props.width / 2),
            ^.svg.y := -(props.height / 2),
            ^.svg.width := props.width,
            ^.svg.height := props.height,
            ^.svg.rx := props.radiusX,
            ^.svg.ry := props.radiusY
          )
        ),
        caption,
        children
      )
    }

  }

  val create = ReactComponentB[Props]("Button")
    .initialState[State](defaultState)
    .renderBackend[Backend]
    .build

}