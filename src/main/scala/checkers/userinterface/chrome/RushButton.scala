package checkers.userinterface.chrome

import checkers.consts.Side
import checkers.userinterface.widgets.Button
import checkers.util.{CssHelpers, SvgHelpers}
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{svg_<^ => svg}

object RushButton {

  private lazy val tooltip = Some("Rush the computer into making a move")

  case class Props(side: Side,
                   centerX: Double,
                   centerY: Double,
                   width: Double,
                   height: Double,
                   onClick: Callback = Callback.empty)

}

class RushButton(button: Button) extends SvgHelpers {

  import RushButton._

  private case class GlyphProps(scale: Double)

  private lazy val RushButtonGlyph = ScalaComponent.builder[GlyphProps]("RushButtonGlyph")
    .render_P { props =>

      val ring = svg.<.circle(
        ^.`class` := "ring",
        svg.^.fill := "none",
        svg.^.r := 0.5.asInstanceOf[JsNumber],
        svg.^.strokeWidth := 0.25.asInstanceOf[JsNumber]
      )

      val slash = svg.<.rect(
        ^.`class` := "slash",
        svg.^.x := (-0.125).asInstanceOf[JsNumber],
        svg.^.y := (-0.45).asInstanceOf[JsNumber],
        svg.^.width := 0.25.asInstanceOf[JsNumber],
        svg.^.height := 0.9 .asInstanceOf[JsNumber]
      )

      svg.<.g(
        ^.`class` := "rush-button-glyph",
        svg.^.transform := s"rotate(-45),scale(${props.scale})",
        slash,
        ring
      )
    }
    .build

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props): VdomElement = {

      val glyphSize = props.height / 2

      val glyph = RushButtonGlyph(GlyphProps(glyphSize))

      val extraClasses: Map[String, Boolean] = Map(
        "rush-button" -> true,
        CssHelpers.playerSideClass(props.side) -> true
      )

      val roundness = 0.27 * math.min(props.width, props.height)

      val buttonProps = Button.Props(
        centerX = props.centerX,
        centerY = props.centerY,
        width = props.width,
        height = props.height,
        radiusX = roundness,
        radiusY = roundness,
        caption = "",
        tooltip = tooltip,
        enabled = true,
        extraClasses = extraClasses,
        onClick =  props.onClick
      )

      button.create(buttonProps)(glyph)
    }
  }

  val create = ScalaComponent.builder[Props]("RushButton")
    .renderBackend[Backend]
    .build

}