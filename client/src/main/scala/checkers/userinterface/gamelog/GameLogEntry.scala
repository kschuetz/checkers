package checkers.userinterface.gamelog

import checkers.consts._
import checkers.userinterface.mixins.{ClipPathHelpers, FontHelpers}
import checkers.userinterface.piece.{PhysicalPiece, PhysicalPieceProps}
import checkers.util.CssHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object GameLogEntry {
  case class Props(widthPixels: Double,
                   heightPixels: Double,
                   turnIndex: Int,
                   side: Side,
                   moveDescription: String)

  private def getStripeClass(props: Props): String =
    if(props.turnIndex % 2 == 0) "even" else "odd"
}

class GameLogEntry(physicalPiece: PhysicalPiece) extends FontHelpers with ClipPathHelpers {
  import GameLogEntry._

  class Backend($: BackendScope[Props, Unit]) {

    def render(props: Props) = {
      val stripe = getStripeClass(props)
      val textHeight = 0.8 * props.heightPixels

      val fontSizeValue = s"${textHeight}px"

      val textBottom = 0.9 * props.heightPixels
      val turnIndexLeft = 0.05 * props.widthPixels
      val turnIndexRight = 0.25 * props.widthPixels
      val descriptionLeft = 0.4 * props.widthPixels
      val descriptionRight = 0.95 * props.widthPixels

      val halfHeight = 0.5 * props.heightPixels
      val avatarX = (turnIndexRight + descriptionLeft) / 2
      val avatarScale = math.min(textHeight, 0.8 * (descriptionLeft - turnIndexRight))

      val backdrop = <.svg.rect(
        ^.key := "backdrop",
        ^.`class` := s"game-log-entry-backdrop $stripe ${CssHelpers.playerSideClass(props.side)}",
        ^.svg.x := 0,
        ^.svg.y := 0,
        ^.svg.width := props.widthPixels,
        ^.svg.height := props.heightPixels
      )

      val turnIndexCaption = s"${props.turnIndex}."

      val turnIndexLabel = <.svg.text(
        ^.`class` := "turn-index",
        ^.svg.x := turnIndexRight,
        ^.svg.y := textBottom,
        ^.svg.textAnchor := "right",
        fontSize := fontSizeValue,
        turnIndexCaption
      )

      val descriptionLabel = <.svg.text(
        ^.`class` := "description",
        ^.svg.x := descriptionLeft,
        ^.svg.y := textBottom,
        ^.svg.textAnchor := "left",
        fontSize := fontSizeValue,
        props.moveDescription
      )

      val clipPathId = s"game-log-clip-path-${props.turnIndex}"

      val textClipPath = <.svg.defs(
        <.svg.clipPathTag(
          ^.id := clipPathId,
          <.svg.rect(
            ^.svg.x := turnIndexLeft,
            ^.svg.y := 0,
            ^.svg.width := descriptionRight - turnIndexLeft,
            ^.svg.height := props.heightPixels
          )
        )
      )

      val avatar = {
        val piece = 1 //MAKEMAN(props.side)
        val avatarProps = PhysicalPieceProps.default.copy(
          piece = piece,
          x = avatarX,
          y = halfHeight,
          scale = avatarScale)
        physicalPiece.create(avatarProps)
      }

      val textElements = <.svg.g(
        clipPathAttr := s"url(#$clipPathId)",
        turnIndexLabel,
        descriptionLabel
      )

      <.svg.g(
        ^.`class` := "game-log-entry",
        backdrop,
        textElements,
        avatar
      )
    }
  }

  val create = ReactComponentB[Props]("GameLogEntry")
    .renderBackend[Backend]
    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, _) =>
      val result = scope.props != nextProps
      CallbackTo.pure(result)
    }
    .build
}