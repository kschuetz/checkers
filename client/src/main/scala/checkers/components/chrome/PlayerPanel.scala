package checkers.components.chrome

import checkers.components.SceneFrame
import checkers.components.piece.{PhysicalPiece, PhysicalPieceProps}
import checkers.consts._
import checkers.models.GameModelReader
import checkers.util.CssHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.SVGSVGElement

import scala.scalajs.js

object PlayerPanel {

  case class Props(widthPixels: Int,
                   heightPixels: Int,
                   color: Color,
                   playerName: String,
                   isComputerPlayer: Boolean,
                   clockDisplay: String,
                   isPlayerTurn: Boolean,
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
      val pieceProps = PhysicalPieceProps.default.copy(
        piece = if(props.color == DARK) DARKMAN else LIGHTMAN,
        ghost = !props.isPlayerTurn,
        scale = scale,
        x = scale + 10,
        y = props.heightPixels / 2
      )
      PhysicalPiece.apply.withKey("avatar")(pieceProps)
    }

    def playerNameDisplay(props: Props) = {

    }

    def clockDisplay(props: Props) = {

    }

    def indicators(props: Props) = {

    }



    def render(props: Props) = {
      val parts = new js.Array[ReactNode]
      parts.push(backdrop(props))
      parts.push(pieceAvatar(props))
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