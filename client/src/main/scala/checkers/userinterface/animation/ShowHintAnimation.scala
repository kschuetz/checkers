package checkers.userinterface.animation

import checkers.consts._
import checkers.core.Board
import checkers.userinterface.piece.{PhysicalPiece, PhysicalPieceProps}
import checkers.userinterface.widgets.Arrow
import checkers.util.{Easing, Point}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object ShowHintAnimation {

  case class Props(fromSquare: Int,
                   toSquare: Int,
                   flashDuration: Double,
                   totalDuration: Double,
                   timeSinceStart: Double)

  private val up = Point(0, -1)

  private val baseClasses= Map("hint-arrow" -> true)

  private val classesHi = baseClasses + ("high" -> true)

  private val classesLo = baseClasses + ("low" -> true)

}

class ShowHintAnimation(arrow: Arrow) {

  import ShowHintAnimation._

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props): ReactElement = {
      val t = props.timeSinceStart / props.flashDuration
      val phase = t - t.toInt
      val classes = if(phase <= 0.5) classesHi else classesLo

      val ptA = Board.squareCenter(props.fromSquare)
      val ptB = Board.squareCenter(props.toSquare)
      val center = ptB - ptA
      val length = center.magnitude
      val dir = center / length
      val angle = {
        val rad = math.acos(dir.dot(up))
        val deg = math.toDegrees(rad)
        if(dir.x < 0) -deg else deg
      }
      val arrowProps = Arrow.Props(length, 0.5, 0.75, 0.25, classes)
      val arrowElement = arrow.create(arrowProps)
      <.svg.g(
        ^.svg.transform := s"translate(${ptA.x},${ptA.y}),rotate($angle)",
        arrowElement
      )
    }
  }

  val create = ReactComponentB[Props]("ShowHintAnimation")
    .renderBackend[Backend]
    .build

}