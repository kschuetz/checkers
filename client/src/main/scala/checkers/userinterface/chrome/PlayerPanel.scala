package checkers.userinterface.chrome

import checkers.consts._
import checkers.core.ApplicationCallbacks
import checkers.userinterface.mixins.FontHelpers
import checkers.util.{CssHelpers, Formatting}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object PlayerPanel extends FontHelpers {

  case class Props(widthPixels: Int,
                   heightPixels: Int,
                   side: Side,
                   playerName: String,
                   isComputerPlayer: Boolean,
                   clockSeconds: Int,
                   scoreDisplay: Option[String],
                   isPlayerTurn: Boolean,
                   waitingForMove: Boolean,
                   endingTurn: Boolean,
                   clockVisible: Boolean,
                   jumpIndicator: Boolean,
                   thinkingIndicator: Boolean,
                   rushButtonEnabled: Boolean,
                   applicationCallbacks: ApplicationCallbacks)

}

class PlayerPanel(pieceAvatar: PieceAvatar,
                  jumpIndicator: JumpIndicator,
                  turnIndicator: TurnIndicator,
                  thinkingIndicator: ThinkingIndicator,
                  rushButton: RushButton) extends FontHelpers {
  import PlayerPanel._

  class Backend($: BackendScope[Props, Unit]) {

    def backdrop(props: Props): ReactElement = {
      <.svg.rect(
        ^.key := "backdrop",
        ^.`class` := s"player-panel-backdrop ${CssHelpers.playerSideClass(props.side)}",
        ^.svg.x := 0,
        ^.svg.y := 0,
        ^.svg.width := props.widthPixels,
        ^.svg.height := props.heightPixels
      )
    }

    def makePieceAvatar(props: Props): ReactElement = {
      val scale = 0.8 * props.heightPixels
      val avatarProps = PieceAvatar.Props(
        side = props.side,
        isPlayerTurn = props.isPlayerTurn,
        scale = scale,
        x = scale - 10,
        y = props.heightPixels / 2
      )

      pieceAvatar.create.withKey("avatar")(avatarProps)
    }

    def makeTurnIndicator(props: Props): ReactElement = {
      val y = props.heightPixels / 2
      val x = 40
      val scale = 0.2 * props.heightPixels
      val turnIndicatorProps = TurnIndicator.Props(side = props.side,
        scale = scale, x = x, y = y, pointsRight = true, endingTurn = props.endingTurn)
      turnIndicator.create.withKey("turn-indicator")(turnIndicatorProps)
    }

    def makeJumpIndicator(props: Props): ReactElement = {
      val x = props.widthPixels - 50
      val y = 0.35 * props.heightPixels
      val jumpIndicatorProps = JumpIndicator.Props(oppositeSide = OPPONENT(props.side),
        x = x,
        y = y,
        scale = 0.37 * props.heightPixels
      )
      jumpIndicator.create.withKey("jump-indicator")(jumpIndicatorProps)
    }

    def makeThinkingIndicator(props: Props): ReactElement = {
      val centerX = 0.575 * props.widthPixels
      val totalWidth = 0.45 * props.widthPixels
      val centerY = 0.8 * props.heightPixels
      val segmentCount = 10
      val segmentWidth = totalWidth / segmentCount
      val offset = (props.clockSeconds % 4) / 4.0
      val thinkingIndicatorProps = ThinkingIndicator.Props(
        side = props.side,
        centerX = centerX,
        centerY = centerY,
        heightPixels = 0.13 * props.heightPixels,
        segmentWidthPixels = segmentWidth,
        segmentCount = segmentCount,
        segmentOffset = offset
      )
      thinkingIndicator.create.withKey("thinking-indicator")(thinkingIndicatorProps)
    }

    def playerNameDisplay(props: Props): ReactElement = {
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

    def clockDisplay(props: Props): ReactElement = {
      val clockText = Formatting.clockDisplay(props.clockSeconds)
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
        clockText
      )
    }

    def scoreDisplay(props: Props): ReactElement = {
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

    def makeRushButton(props: Props): ReactElement = {
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
      rushButton.create.withKey("rush-button")(rushButtonProps)
    }

    def render(props: Props): ReactElement = {
      val parts = new js.Array[ReactNode]
      parts.push(backdrop(props))
      parts.push(makePieceAvatar(props))
      parts.push(playerNameDisplay(props))
      if(props.isPlayerTurn) {
        parts.push(makeTurnIndicator(props))
      }
      if(props.jumpIndicator) {
        parts.push(makeJumpIndicator(props))
      }
      if(props.thinkingIndicator) {
        parts.push(makeThinkingIndicator(props))
      }
      if(props.scoreDisplay.nonEmpty) {
        parts.push(scoreDisplay(props))
      }
      if(props.clockVisible) {
        parts.push(clockDisplay(props))
      }
      if(props.rushButtonEnabled) {
        parts.push(makeRushButton(props))
      }
      <.svg.g(
        parts
      )
    }

    private def getClasses(props: Props, base: String): String = {
      s"$base ${CssHelpers.playerSideClass(props.side)} ${CssHelpers.turnStatus(props.isPlayerTurn)}"
    }
  }

  val create = ReactComponentB[Props]("PlayerPanel")
    .renderBackend[Backend]
    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, _) =>
      val result = scope.props != nextProps
      CallbackTo.pure(result)
    }
    .build

}