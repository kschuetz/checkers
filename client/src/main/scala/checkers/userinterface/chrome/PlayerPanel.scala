package checkers.userinterface.chrome

import checkers.userinterface.mixins.FontHelpers
import checkers.consts._
import checkers.core.ApplicationCallbacks
import checkers.util.CssHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object PlayerPanel extends FontHelpers {

  case class Props(widthPixels: Int,
                   heightPixels: Int,
                   side: Side,
                   playerName: String,
                   isComputerPlayer: Boolean,
                   clockDisplay: String,
                   scoreDisplay: Option[String],
                   isPlayerTurn: Boolean,
                   waitingForMove: Boolean,
                   endingTurn: Boolean,
                   jumpIndicator: Boolean,
                   thinkingIndicator: Boolean,
                   rushButtonEnabled: Boolean,
                   applicationCallbacks: ApplicationCallbacks)

  class PlayerPanelBackend($: BackendScope[Props, Unit]) {

    def backdrop(props: Props) = {
      <.svg.rect(
        ^.key := "backdrop",
        ^.`class` := s"player-panel-backdrop ${CssHelpers.playerSideClass(props.side)}",
        ^.svg.x := 0,
        ^.svg.y := 0,
        ^.svg.width := props.widthPixels,
        ^.svg.height := props.heightPixels
      )
    }

    def pieceAvatar(props: Props) = {
      val scale = 0.8 * props.heightPixels
      val avatarProps = PieceAvatar.Props(
        side = props.side,
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
      val turnIndicatorProps = TurnIndicator.Props(side = props.side,
        scale = scale, x = x, y = y, pointsRight = true, endingTurn = props.endingTurn)
      TurnIndicator.component.withKey("turn-indicator")(turnIndicatorProps)
    }

    def jumpIndicator(props: Props) = {
      val x = props.widthPixels - 50
      val y = 0.35 * props.heightPixels
      val jumpIndicatorProps = JumpIndicator.Props(oppositeSide = OPPONENT(props.side),
        x = x,
        y = y,
        scale = 0.37 * props.heightPixels
      )
      JumpIndicator.component.withKey("jump-indicator")(jumpIndicatorProps)
    }

    def playerNameDisplay(props: Props) = {
      val textHeight = 0.27 * props.heightPixels
      val x = props.widthPixels * 0.24
      //val y = props.heightPixels / 2
      val y = 0.5889 * props.heightPixels

      <.svg.text(
        ^.key := "player-name",
        ^.`class` := getClasses(props, "player-name-label"),
        ^.svg.x := x,
        ^.svg.y := y,
        ^.svg.textAnchor := "left",
        //props.isPlayerTurn ?= (fontWeight := "bold"),
        fontSize := s"${textHeight}px",
        props.playerName
      )
    }

    def clockDisplay(props: Props) = {
      val textHeight = 0.17 * props.heightPixels
      val x = props.widthPixels * 0.24
      //val y = props.heightPixels / 2
      val y = 0.86 * props.heightPixels
      <.svg.text(
        ^.key := "clock-display",
        ^.`class` := getClasses(props, "clock-display"),
        ^.svg.x := x,
        ^.svg.y := y,
        ^.svg.textAnchor := "left",
        fontSize := s"${textHeight}px",
        props.clockDisplay
      )
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

    def rushButton(props: Props) = {
      val x = props.widthPixels - 50
      val y = 0.75 * props.heightPixels
      val size = 0.37 * props.heightPixels
      val rushButtonProps = RushButton.Props(
        side = props.side,
        centerX = x,
        centerY = y,
        width = size,
        height = size,
        onClick = props.applicationCallbacks.onRushButtonClicked
      )
      RushButton.component.withKey("rush-button")(rushButtonProps)
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
      if(props.clockDisplay.nonEmpty) {
        parts.push(clockDisplay(props))
      }
      if(props.rushButtonEnabled) {
        parts.push(rushButton(props))
      }
      <.svg.g(
        parts
      )
    }

    private def getClasses(props: Props, base: String): String = {
      s"$base ${CssHelpers.playerSideClass(props.side)} ${CssHelpers.turnStatus(props.isPlayerTurn)}"
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