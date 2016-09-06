package checkers.components.chrome

import checkers.components.{SceneContainerContext, SceneFrame}
import checkers.components.piece.{PhysicalPiece, PhysicalPieceProps}
import checkers.consts._
import checkers.core.GameModelReader
import checkers.geometry.Point
import checkers.util.{CssHelpers, SvgHelpers}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.SVGSVGElement

import scala.scalajs.js

object Button extends SvgHelpers {

  protected val fontSize = "fontSize".reactStyle


  case class Props(centerX: Double,
                   centerY: Double,
                   width: Double,
                   height: Double,
                   caption: String = "",
                   tooltip: Option[String] = None)

  case class State(depressed: Boolean)

  private val defaultState = State(depressed = false)

  class ButtonBackend($: BackendScope[Props, State]) {
    def handleMouseDown(e: ReactEventI) = {
      $.modState(_.copy(depressed = true))
    }

    def handleMouseUp(e: ReactEventI) = {
      $.modState(_.copy(depressed = false))
    }

    def handleMouseOut(e: ReactEventI) = {
      $.modState(_.copy(depressed = false))
    }

    def render(props: Props, state: State) = {
      val textHeight = math.round(2 * props.height / 3)
      val textY = 4 + (props.height - textHeight) / 2

      val (centerX, centerY) = if(state.depressed) {
        (props.centerX + 3, props.centerY + 3)
      } else {
        (props.centerX, props.centerY)
      }

      <.svg.g(
        ^.`class` := "button enabled",
        ^.svg.transform := s"translate($centerX,$centerY)",
        ^.onMouseDown ==> handleMouseDown,
        ^.onMouseUp ==> handleMouseUp,
        ^.onMouseOut ==> handleMouseOut,

        <.titleTag(props.tooltip),

        <.svg.rect(
          ^.svg.x := -(props.width / 2),
          ^.svg.y := -(props.height / 2),
          ^.svg.width := props.width,
          ^.svg.height := props.height,
          ^.svg.rx := 10,
          ^.svg.ry := 10
        ),
        <.svg.text(
          ^.svg.y := textY,
          fontSize := s"${textHeight}px",
          props.caption
        )
      )
    }

  }

  val component = ReactComponentB[Props]("Button")
    .initialState[State](defaultState)
    .renderBackend[ButtonBackend]
    .build

  def apply(props: Props) = component(props)




}