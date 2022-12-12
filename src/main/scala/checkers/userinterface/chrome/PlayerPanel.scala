package checkers.userinterface.chrome

import checkers.consts._
import checkers.core.ApplicationCallbacks
import checkers.userinterface.mixins.FontHelpers
import checkers.util.{CssHelpers, Formatting}
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{svg_<^ => svg}

object PlayerPanel {

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

    def backdrop(props: Props): VdomElement = {
      svg.<.rect(
        ^.key := "backdrop",
        ^.`class` := s"player-panel-backdrop ${CssHelpers.playerSideClass(props.side)}",
        svg.^.x := 0.asInstanceOf[JsNumber],
        svg.^.y := 0.asInstanceOf[JsNumber],
        svg.^.width := props.widthPixels.asInstanceOf[JsNumber],
        svg.^.height := props.heightPixels.asInstanceOf[JsNumber]
      )
    }

    def makePieceAvatar(props: Props): VdomElement = {
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

    def makeTurnIndicator(props: Props): VdomElement = {
      val y = props.heightPixels / 2
      val x = 0.1 * props.widthPixels
      val scale = 0.2 * props.heightPixels
      val turnIndicatorProps = TurnIndicator.Props(side = props.side,
        scale = scale, x = x, y = y, pointsRight = true, endingTurn = props.endingTurn)
      turnIndicator.create.withKey("turn-indicator")(turnIndicatorProps)
    }

    def makeJumpIndicator(props: Props): VdomElement = {
      val x = 0.875 * props.widthPixels
      val y = 0.35 * props.heightPixels
      val jumpIndicatorProps = JumpIndicator.Props(oppositeSide = OPPONENT(props.side),
        x = x,
        y = y,
        scale = 0.37 * props.heightPixels
      )
      jumpIndicator.create.withKey("jump-indicator")(jumpIndicatorProps)
    }

    def makeThinkingIndicator(props: Props): VdomElement = {
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

    def playerNameDisplay(props: Props): VdomElement = {
      val textHeight = 0.27 * props.heightPixels
      val x = props.widthPixels * 0.24
      //val y = props.heightPixels / 2
      val y = 0.5889 * props.heightPixels

      svg.<.text(
        ^.key := "player-name",
        ^.`class` := getClasses(props, "player-name-label"),
        svg.^.x := x.asInstanceOf[JsNumber],
        svg.^.y := y.asInstanceOf[JsNumber],
        svg.^.textAnchor := "left",
        //props.isPlayerTurn ?= (fontWeight := "bold"),
        fontSize := s"${textHeight}px",
        props.playerName
      )
    }

    def clockDisplay(props: Props): VdomElement = {
      val clockText = Formatting.clockDisplay(props.clockSeconds)
      val textHeight = 0.17 * props.heightPixels
      val x = props.widthPixels * 0.24
      //val y = props.heightPixels / 2
      val y = 0.86 * props.heightPixels
      svg.<.text(
        ^.key := "clock-display",
        ^.`class` := getClasses(props, "clock-display"),
        svg.^.x := x.asInstanceOf[JsNumber],
        svg.^.y := y.asInstanceOf[JsNumber],
        svg.^.textAnchor := "left",
        fontSize := s"${textHeight}px",
        clockText
      )
    }

    def scoreDisplay(props: Props): VdomElement = {
      val displayText = props.scoreDisplay.getOrElse("")
      val x = props.widthPixels - 5
      val y = props.heightPixels - 7
      svg.<.g(
        ^.key := "score",
        svg.^.textAnchor := "end",
        svg.^.transform := s"translate($x,$y)",
        svg.<.text(displayText)
      )
    }

    def makeRushButton(props: Props): VdomElement = {
      val x = 0.875 * props.widthPixels
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

    def render(props: Props): VdomElement = {
      val parts = VdomArray.empty
      parts += backdrop(props)
      parts += makePieceAvatar(props)
      parts += playerNameDisplay(props)
      if(props.isPlayerTurn) {
        parts += makeTurnIndicator(props)
      }
      if(props.jumpIndicator) {
        parts += makeJumpIndicator(props)
      }
      if(props.thinkingIndicator) {
        parts += makeThinkingIndicator(props)
      }
      if(props.scoreDisplay.nonEmpty) {
        parts += scoreDisplay(props)
      }
      if(props.clockVisible) {
        parts += clockDisplay(props)
      }
      if(props.rushButtonEnabled) {
        parts += makeRushButton(props)
      }
      svg.<.g(
        parts
      )
    }

    private def getClasses(props: Props, base: String): String = {
      s"$base ${CssHelpers.playerSideClass(props.side)} ${CssHelpers.turnStatus(props.isPlayerTurn)}"
    }
  }

  val create = ScalaComponent.builder[Props]("PlayerPanel")
    .renderBackend[Backend]
    .shouldComponentUpdate { x => CallbackTo.pure(x.cmpProps(_ != _)) }
    .build

}