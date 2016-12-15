package checkers.userinterface.chrome

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object PowerMeter {

  case class Props(centerX: Double,
                   centerY: Double,
                   widthPixels: Double,
                   heightPixels: Double,
                   position: Double,   // -1 (light advantage) to 1 (dark advantage)
                   tooltip: Option[String])

  def getPosition(darkScore: Int, lightScore: Int): Double = {
    val darkAdvantage = (darkScore - lightScore) / 2d
    2 * (sigmoid(darkAdvantage / 400d) - 0.5)
  }

  private def sigmoid(t: Double): Double = 1d / (1 + math.exp(-t))

}

class PowerMeter {
  import PowerMeter._

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props): ReactElement = {
      val width = props.widthPixels
      val height = props.heightPixels
      val halfWidth = 0.5 * width
      val position = math.max(-1, math.min(1, props.position))

      val left = props.centerX - halfWidth
      val right = left + width
      val top = props.centerY - 0.5 * height
      val splitX = props.centerX + position * halfWidth

      val darkSide: Option[ReactElement] = if(position > -1) {
        Some(<.svg.rect(
          ^.`class` := "power-meter-side dark",
          ^.svg.x := left,
          ^.svg.y := top,
          ^.svg.width := splitX - left,
          ^.svg.height := height
        ))
      } else None

      val lightSide: Option[ReactElement] = if(position < 1) {
        Some(<.svg.rect(
          ^.`class` := "power-meter-side light",
          ^.svg.x := splitX,
          ^.svg.y := top,
          ^.svg.width := right - splitX,
          ^.svg.height := height
        ))
      } else None

      val backdrop = <.svg.rect(
        ^.`class` := "power-meter-backdrop",
        ^.svg.x := left,
        ^.svg.y := top,
        ^.svg.width := width,
        ^.svg.height := height
      )

      val centerMark = <.svg.line(
        ^.`class` := "power-meter-center-mark",
        ^.svg.x1 := props.centerX,
        ^.svg.y1 := top,
        ^.svg.x2 := props.centerX,
        ^.svg.y2 := top + height
      )

      val tooltipElement = props.tooltip.map { text =>
        <.titleTag(text)
      }

      <.svg.g(
        ^.`class` := "power-meter",
        tooltipElement,
        backdrop,
        darkSide,
        lightSide,
        centerMark
      )
    }
  }

  val create = ReactComponentB[Props]("PowerMeter")
    .renderBackend[Backend]
    .build
}