package checkers.components.chrome

import checkers.components.SceneFrame
import checkers.components.mixins.FontHelpers
import checkers.components.piece.{PhysicalPiece, PhysicalPieceProps}
import checkers.consts._
import checkers.core.GameModelReader
import checkers.util.CssHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.SVGSVGElement

import scala.scalajs.js

object PlayerPanel extends FontHelpers {

  case class Props(widthPixels: Int,
                   heightPixels: Int,
                   color: Color,
                   playerName: String,
                   isComputerPlayer: Boolean,
                   clockDisplay: String,
                   scoreDisplay: Option[String],
                   isPlayerTurn: Boolean,
                   endingTurn: Boolean,
                   jumpIndicator: Boolean,
                   thinkingIndicator: Boolean)

  class PlayerPanelBackend($: BackendScope[Props, Unit]) {

    def backdrop(props: Props) = {
      <.svg.rect(
        ^.key := "backdrop",
        ^.`class` := s"player-panel-backdrop ${CssHelpers.playerColorClass(props.color)}",
        ^.svg.x := 0,
        ^.svg.y := 0,
        ^.svg.width := props.widthPixels,
        ^.svg.height := props.heightPixels
      )
    }

    def pieceAvatar(props: Props) = {
      val scale = 0.8 * props.heightPixels
      val avatarProps = PieceAvatar.Props(
        color = props.color,
        isPlayerTurn = props.isPlayerTurn,
        scale = scale,
        x = scale - 10,
        y = props.heightPixels / 2
      )

      PieceAvatar.component.withKey("avatar")(avatarProps)
    }

    def turnIndicator(props: Props) = {
      val y = props.heightPixels / 2
      val x = 40
      val scale = 0.2 * props.heightPixels
      val turnIndicatorProps = TurnIndicator.Props(color = props.color,
        scale = scale, x = x, y = y, pointsRight = true, endingTurn = props.endingTurn)
      TurnIndicator.component.withKey("turn-indicator")(turnIndicatorProps)
    }

    def jumpIndicator(props: Props) = {
      val x = props.widthPixels - 50
      val y = props.heightPixels / 2
      val jumpIndicatorProps = JumpIndicator.Props(opponentColor = OPPONENT(props.color),
        x = x,
        y = y,
        scale = 0.4 * props.heightPixels
      )
      JumpIndicator.component.withKey("jump-indicator")(jumpIndicatorProps)
    }

    def playerNameDisplay(props: Props) = {
      val textHeight = 0.27 * props.heightPixels
      val x = props.widthPixels * 0.24
      val y = props.heightPixels / 2

      <.svg.text(
        ^.key := "player-name",
        ^.`class` := s"player-name-label ${CssHelpers.playerColorClass(props.color)}",
        ^.svg.x := x,
        ^.svg.y := y,
        ^.svg.textAnchor := "left",
        props.isPlayerTurn ?= (fontWeight := "bold"),
        fontSize := s"${textHeight}px",
        props.playerName
      )
    }

    def clockDisplay(props: Props) = {

    }

    def indicators(props: Props) = {

    }

    def scoreDisplay(props: Props) = {
      val displayText = props.scoreDisplay.getOrElse("")
      val x = props.widthPixels - 5
      val y = props.heightPixels - 7
      <.svg.g(
        ^.key := "score",
        ^.svg.textAnchor := "end",
        ^.svg.transform := s"translate($x,$y)",
        <.svg.text(displayText)
      )
    }

    def render(props: Props) = {
      val parts = new js.Array[ReactNode]
      parts.push(backdrop(props))
      parts.push(pieceAvatar(props))
      parts.push(playerNameDisplay(props))
      if(props.isPlayerTurn) {
        parts.push(turnIndicator(props))
      }
      if(props.jumpIndicator) {
        parts.push(jumpIndicator(props))
      }
      if(props.scoreDisplay.nonEmpty) {
        parts.push(scoreDisplay(props))
      }
      <.svg.g(
        parts
      )
    }
  }

  val component = ReactComponentB[Props]("PlayerPanel")
    .renderBackend[PlayerPanelBackend]
    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, _) =>
      val result = scope.props != nextProps
      CallbackTo.pure(result)
    }
    .build

  def apply(props: Props) = component(props)


}