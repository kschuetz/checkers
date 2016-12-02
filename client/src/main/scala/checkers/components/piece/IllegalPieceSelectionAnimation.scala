package checkers.components.piece

import checkers.consts._
import checkers.core.Board
import checkers.geometry.Point
import checkers.util.CssHelpers
import japgolly.scalajs.react._
import checkers.util.SvgHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object IllegalPieceSelectionAnimation {


  private val NoSymbolLeg = ReactComponentB[String]("NoSymbolLeg")
    .render_P { transform =>
      <.svg.rect(
        ^.svg.x := -0.17,
        ^.svg.y := -0.5,
        ^.svg.width := 0.34,
        ^.svg.height := 1,
        ^.svg.transform := transform
      )
    }
    .build

  private val NoSymbol = ReactComponentB[Side]("NoSymbol")
    .render_P { side =>
      val classes = s"no-symbol ${CssHelpers.playerSideClass(side)}"
      <.svg.g(
        ^.`class` := classes,
        NoSymbolLeg("skewX(45)"),
        NoSymbolLeg("skewX(-45)"),
        ^.svg.transform := "scale(0.63)"
      )
    }
    .build

  case class Props(piece: Occupant,
                   squareIndex: Int,
                   progress: Double,
                   rotationDegrees: Double = 0)

  class IllegalPieceSelectionAnimationBackend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      val t = props.progress
      val xoffset = 0.07 * (1 - t) * math.sin(40.5 * t)
      val yoffset = 0

      val physicalPieceProps = PhysicalPieceProps.default.copy(piece = props.piece,
        x = xoffset,
        y = yoffset,
        rotationDegrees = props.rotationDegrees)
      val physicalPiece = PhysicalPiece.apply(physicalPieceProps)

      val pt = Board.squareCenter(props.squareIndex)

      val noSymbolShowing = (t >= 0.1 && t <= 0.3) || (t >= 0.5 && t <= 0.7)

      <.svg.g(
        physicalPiece,
        if(noSymbolShowing) NoSymbol(SIDE(props.piece)) else EmptyTag,
        ^.svg.transform := s"translate(${pt.x},${pt.y})"
      )

    }
  }


  val component = ReactComponentB[Props]("IllegalPieceSelectionAnimation")
    .renderBackend[IllegalPieceSelectionAnimationBackend]
    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, _) =>
      val result = scope.props != nextProps
      CallbackTo.pure(result)
    }
    .build

  def apply(props: Props) = component(props)

}