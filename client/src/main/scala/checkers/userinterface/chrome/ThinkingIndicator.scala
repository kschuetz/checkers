package checkers.userinterface.chrome

import checkers.consts._
import checkers.userinterface.mixins.ClipPathHelpers
import checkers.util.{CssHelpers, SvgHelpers}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.VdomAttr
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

object ThinkingIndicator {

  case class Props(side: Side,
                   centerX: Double,
                   centerY: Double,
                   heightPixels: Double,
                   segmentWidthPixels: Double,
                   segmentCount: Int,
                   segmentOffset: Double)

  private val Roundness: Double = 7 / 11.7

}

class ThinkingIndicator extends SvgHelpers with ClipPathHelpers {

  import ThinkingIndicator._


  private case class SegmentGroupProps(height: Double,
                                       segmentWidth: Double,
                                       segmentCount: Int)

  private val SegmentGroup = ScalaComponent.build[SegmentGroupProps]("ThinkingIndicatorSegmentGroup")
    .render_P { props =>
      val height = props.height
      val y = -height / 2
      val segmentCount = 2 + props.segmentCount   // 1 extra on both the left and right
      val segmentWidth = props.segmentWidth
      val halfSegmentWidth = segmentWidth / 2
      var x = -(segmentCount * halfSegmentWidth)
      var i = 0
      val segments = new js.Array[VdomNode]
      while(i < segmentCount) {
        val even = <.svg.rect(
          ^.key := 2 * i,
          ^.`class` := "segment even",
          ^.svg.x := x,
          ^.svg.y := y,
          ^.svg.width := halfSegmentWidth,
          ^.svg.height := height
        )
        segments.push(even)
        val odd = <.svg.rect(
          ^.key := 2 * i + 1,
          ^.`class` := "segment odd",
          ^.svg.x := x + halfSegmentWidth,
          ^.svg.y := y,
          ^.svg.width := halfSegmentWidth,
          ^.svg.height := height
        )
        segments.push(odd)
        x += segmentWidth
        i += 1
      }
      <.svg.g(
        ^.`class` := "segment-group",
        ^.svg.transform := "skewX(-45)",
        segments
      )
    }
    .shouldComponentUpdateConst { case ShouldComponentUpdate(scope, nextProps, _) =>
      CallbackTo.pure(scope.props != nextProps)
    }
    .build

  class Backend($: BackendScope[Props, Unit]) {

    def render(props: Props): VdomElement = {
      val totalWidth = props.segmentCount * props.segmentWidthPixels
      val left = -totalWidth / 2
      val top = -props.heightPixels / 2

      val clipPathId = s"ti-clip-path-${CssHelpers.playerSideClass(props.side)}"

      val r = math.floor(Roundness * props.heightPixels).toInt

      val clipPath = <.svg.defs(
        <.svg.clipPathTag(
          ^.id := clipPathId,
          <.svg.rect(
            ^.svg.x := left,
            ^.svg.y := top,
            ^.svg.width := totalWidth,
            ^.svg.height := props.heightPixels,
            ^.svg.rx := r,
            ^.svg.ry := r
          )
        )
      )

      val border = <.svg.rect(
        ^.`class` := "border",
        ^.svg.x := left,
        ^.svg.y := top,
        ^.svg.width := totalWidth,
        ^.svg.height := props.heightPixels,
        ^.svg.rx := r,
        ^.svg.ry := r
      )

      val segmentGroup = {
        val offsetPixels = props.segmentOffset * props.segmentWidthPixels
        val sgProps = SegmentGroupProps(props.heightPixels, props.segmentWidthPixels, props.segmentCount)
        <.svg.g(
          ^.svg.transform := s"translate($offsetPixels, 0)",
          SegmentGroup(sgProps)
        )
      }

      val clippedGroup = <.svg.g(
        clipPathAttr := s"url(#$clipPathId)",
        segmentGroup
      )

      <.svg.g(
        ^.`class` := s"thinking-indicator ${CssHelpers.playerSideClass(props.side)}",
        ^.svg.transform := s"translate(${props.centerX},${props.centerY})",
        clipPath,
        border,
        clippedGroup
      )
    }

  }

  val create = ScalaComponent.build[Props]("ThinkingIndicator")
    .renderBackend[Backend]
    .build
}
