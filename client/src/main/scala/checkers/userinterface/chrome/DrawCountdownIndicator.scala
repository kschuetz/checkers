package checkers.userinterface.chrome

import checkers.userinterface.mixins.{ClipPathHelpers, FontHelpers}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object DrawCountdownIndicator {
  case class Props(centerX: Double,
                   centerY: Double,
                   widthPixels: Double,
                   heightPixels: Double,
                   movesUntilDraw: Int)

  private val clipPathId = "draw-indicator-clip-path"

}

class DrawCountdownIndicator extends FontHelpers with ClipPathHelpers {

  import DrawCountdownIndicator._

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props): ReactElement = {
      val halfWidth = 0.5 * props.widthPixels
      val halfHeight = 0.5 * props.heightPixels

      val x1 = 0.25 * props.widthPixels
      val x2 = 0.28 * props.widthPixels

      val textBottom = 0.8 * props.heightPixels

      val textHeight1 = 0.8 * props.heightPixels

      val textHeight2 = halfHeight

      val valueLabel = <.svg.text(
        ^.`class` := "countdown-value",
        ^.svg.x := x1,
        ^.svg.y := textBottom,
        ^.svg.textAnchor := "end",
        fontSize := textHeightPixels(textHeight1),
        props.movesUntilDraw.toString
      )

      val captionText = if(props.movesUntilDraw == 1) "move until draw" else "moves until draw"

      val caption = <.svg.text(
        ^.`class` := "countdown-caption",
        ^.svg.x := x2,
        ^.svg.y := textBottom,
        fontSize := textHeightPixels(textHeight2),
        captionText
      )

      val textClipPath = <.svg.defs(
        <.svg.clipPathTag(
          ^.id := clipPathId,
          <.svg.rect(
            ^.svg.x := 0,
            ^.svg.y := 0,
            ^.svg.width := props.widthPixels,
            ^.svg.height := props.heightPixels
          )
        )
      )

      val textElements = <.svg.g(
        clipPathAttr := s"url(#$clipPathId)",
        caption,
        valueLabel
      )

      val backdrop =  <.svg.rect(
        ^.`class` := "draw-countdown-indicator-backdrop",
        ^.svg.x := 0,
        ^.svg.y := 0,
        ^.svg.width := props.widthPixels,
        ^.svg.height := props.heightPixels
      )

      val translateX = props.centerX - halfWidth
      val translateY = props.centerY - halfHeight
      val transform = s"translate($translateX,$translateY)"

     <.svg.g(
       ^.`class` := "draw-countdown-indicator",
       ^.svg.transform := transform,
       textClipPath,
       backdrop,
       textElements
     )
    }
  }

  val create = ReactComponentB[Props]("DrawCountdownIndicator")
    .renderBackend[Backend]
    .build
}