package checkers.userinterface.chrome

import checkers.userinterface.mixins.{ClipPathHelpers, FontHelpers}
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{svg_<^ => svg}

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
    def render(props: Props): VdomElement = {
      val halfWidth = 0.5 * props.widthPixels
      val halfHeight = 0.5 * props.heightPixels

      val x1 = 0.25 * props.widthPixels
      val x2 = 0.28 * props.widthPixels

      val textBottom = 0.8 * props.heightPixels

      val textHeight1 = 0.8 * props.heightPixels

      val textHeight2 = halfHeight

      val valueLabel = svg.<.text(
        ^.`class` := "countdown-value",
        svg.^.x := x1.asInstanceOf[JsNumber],
        svg.^.y := textBottom.asInstanceOf[JsNumber],
        svg.^.textAnchor := "end",
        fontSize := textHeightPixels(textHeight1),
        props.movesUntilDraw.toString
      )

      val captionText = if(props.movesUntilDraw == 1) "move until draw" else "moves until draw"

      val caption = svg.<.text(
        ^.`class` := "countdown-caption",
        svg.^.x := x2.asInstanceOf[JsNumber],
        svg.^.y := textBottom.asInstanceOf[JsNumber],
        fontSize := textHeightPixels(textHeight2),
        captionText
      )

      val textClipPath = svg.<.defs(
        svg.<.clipPathTag(
          ^.id := clipPathId,
          svg.<.rect(
            svg.^.x := 0.asInstanceOf[JsNumber],
            svg.^.y := 0.asInstanceOf[JsNumber],
            svg.^.width := props.widthPixels.asInstanceOf[JsNumber],
            svg.^.height := props.heightPixels.asInstanceOf[JsNumber]
          )
        )
      )

      val textElements = svg.<.g(
        clipPathAttr := s"url(#$clipPathId)",
        caption,
        valueLabel
      )

      val backdrop =  svg.<.rect(
        ^.`class` := "draw-countdown-indicator-backdrop",
        svg.^.x := 0.asInstanceOf[JsNumber],
        svg.^.y := 0.asInstanceOf[JsNumber],
        svg.^.width := props.widthPixels.asInstanceOf[JsNumber],
        svg.^.height := props.heightPixels.asInstanceOf[JsNumber]
      )

      val translateX = props.centerX - halfWidth
      val translateY = props.centerY - halfHeight
      val transform = s"translate($translateX,$translateY)"

     svg.<.g(
       ^.`class` := "draw-countdown-indicator",
       svg.^.transform := transform,
       textClipPath,
       backdrop,
       textElements
     )
    }
  }

  val create = ScalaComponent.build[Props]("DrawCountdownIndicator")
    .renderBackend[Backend]
    .build
}