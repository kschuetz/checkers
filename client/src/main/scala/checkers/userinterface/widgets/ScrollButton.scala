package checkers.userinterface.widgets

import checkers.util.{Point, SvgHelpers}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object ScrollButton {
  case class Props(centerX: Double,
                   centerY: Double,
                   width: Double,
                   height: Double,
                   up: Boolean,
                   onClick: Callback = Callback.empty)
}

class ScrollButton(button: Button) extends SvgHelpers {
  import ScrollButton._

  private case class GlyphProps(width: Double, height: Double, up: Boolean)

  private lazy val ScrollButtonGlyph = ScalaComponent.build[GlyphProps]("ScrollButtonGlyph")
    .render_P { props =>

      val halfHeight = props.height / 2
      val halfWidth = props.width / 2

      val top = if(props.up) -halfHeight else halfHeight
      val bottom = -top

      val pathString = pointsToPathString(
        Point(0, top),
        Point(halfWidth, bottom),
        Point(-halfWidth, bottom))

      val glyph = <.svg.polygon(
        ^.svg.points := pathString
      )

      <.svg.g(
        ^.`class` := (if(props.up) "scroll-button-glyph up" else "scroll-button-glyph down"),
        glyph
      )
    }
    .build

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props): VdomElement = {

      val glyphWidth = 0.8 * props.width
      val glyphHeight  = 0.6 * props.height
      val radius = 0.25 * math.min(props.width, props.height)

      val glyphProps = GlyphProps(glyphWidth, glyphHeight, props.up)

      val glyph = ScrollButtonGlyph(glyphProps)

      val extraClasses: Map[String, Boolean] = Map(
        "scroll-button" -> true,
        "up" -> props.up,
        "down" -> !props.up
      )

      val buttonProps = Button.Props(
        centerX = props.centerX,
        centerY = props.centerY,
        width = props.width,
        height = props.height,
        radiusX = radius,
        radiusY = radius,
        caption = "",
        tooltip = None,
        enabled = true,
        extraClasses = extraClasses,
        onClick = props.onClick
      )

      button.create(buttonProps, glyph)
    }
  }

  val create = ScalaComponent.build[Props]("ScrollButton")
    .renderBackend[Backend]
    .build

}
