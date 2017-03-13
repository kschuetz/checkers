package checkers.userinterface.animation

import checkers.core.Board
import checkers.userinterface.widgets.DirectedArrow
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.VdomElement

object ShowHintAnimation {

  case class Props(fromSquare: Int,
                   toSquare: Int,
                   flashDuration: Double,
                   totalDuration: Double,
                   timeSinceStart: Double)

  private val baseClasses = Map("hint-arrow" -> true)

  private val classesHi = baseClasses + ("high" -> true)

  private val classesLo = baseClasses + ("low" -> true)

}

class ShowHintAnimation(directedArrow: DirectedArrow) {

  import ShowHintAnimation._

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props): VdomElement = {
      val t = props.timeSinceStart / props.flashDuration
      val phase = t - t.toInt
      val classes = if(phase <= 0.5) classesHi else classesLo

      val ptA = Board.squareCenter(props.fromSquare)
      val ptB = Board.squareCenter(props.toSquare)
      val arrowProps = DirectedArrow.Props(
        source = ptA, dest = ptB, headLength = 0.5, headWidth = 0.75, baseWidth = 0.25,
        sourceMargin = 0.115, destMargin = 0.115, classes)

      directedArrow.create(arrowProps)
    }
  }

  val create = ScalaComponent.build[Props]("ShowHintAnimation")
    .renderBackend[Backend]
    .build

}