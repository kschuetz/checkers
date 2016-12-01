package checkers.components.chrome

import checkers.consts.Color
import checkers.util.{CssHelpers, SvgHelpers}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object RushButton extends SvgHelpers {

  private lazy val tooltip = Some("Rush the computer into making a move")

  case class Props(color: Color,
                   centerX: Double,
                   centerY: Double,
                   width: Double,
                   height: Double,
                   onClick: Callback = Callback.empty)

  private case class GlyphProps(scale: Double)

  private lazy val RushButtonGlyph = ReactComponentB[GlyphProps]("RushButtonGlyph")
    .render_P { props =>

      val ring = <.svg.circle(
        ^.`class` := "ring",
        ^.svg.fill := "none",
        ^.svg.r := 0.5,
        ^.svg.strokeWidth := 0.25
      )

      val slash = <.svg.rect(
        ^.`class` := "slash",
        ^.svg.x := -0.125,
        ^.svg.y := -0.45,
        ^.svg.width := 0.25,
        ^.svg.height := 0.9
      )

      <.svg.g(
        ^.`class` := "rush-button-glyph",
        ^.svg.transform := s"rotate(-45),scale(${props.scale})",
        slash,
        ring
      )
    }
    .build

  class RushButtonBackend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {

      val glyphSize = props.height / 2

      val glyph = RushButtonGlyph(GlyphProps(glyphSize))

      val extraClasses: Map[String, Boolean] = Map(
        "rush-button" -> true,
        CssHelpers.playerColorClass(props.color) -> true
      )

      val buttonProps = Button.Props(
        centerX = props.centerX,
        centerY = props.centerY,
        width = props.width,
        height = props.height,
        caption = "",
        tooltip = tooltip,
        enabled = true,
        extraClasses = extraClasses,
        onClick =  props.onClick
      )

      Button.component(buttonProps, glyph)
    }
  }

  val component = ReactComponentB[Props]("RushButton")
    .renderBackend[RushButtonBackend]
    .build

  def apply(props: Props) = component(props)

}