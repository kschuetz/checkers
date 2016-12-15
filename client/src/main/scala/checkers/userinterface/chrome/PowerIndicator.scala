package checkers.userinterface.chrome

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object PowerIndicator {

  case class Props(centerX: Double,
                   centerY: Double,
                   widthPixels: Double,
                   heightPixels: Double,
                   position: Double,   // -1 (dark) to 1 (light)
                   tooltip: Option[String])

}

class PowerIndicator {
  import PowerIndicator._

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props): ReactElement = {
      <.svg.g()
    }
  }

  val create = ReactComponentB[Props]("PowerIndicator")
    .renderBackend[Backend]
    .build
}