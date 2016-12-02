package checkers.userinterface.chrome

import checkers.consts._
import checkers.core.{ApplicationCallbacks, GameOverState}
import checkers.userinterface.mixins.FontHelpers
import checkers.userinterface.piece.{PhysicalPiece, PhysicalPieceProps}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object GameOverPanel {

  private val darkWinPieces = Vector(DARKKING, DARKKING, DARKKING, DARKKING)
  private val lightWinPieces = Vector(LIGHTKING, LIGHTKING, LIGHTKING, LIGHTKING)
  private val drawPieces = Vector(DARKMAN, LIGHTMAN, DARKMAN, LIGHTMAN)

  case class Props(widthPixels: Int,
                   heightPixels: Int,
                   gameOverState: GameOverState,
                   applicationCallbacks: ApplicationCallbacks)

}

class GameOverPanel(physicalPiece: PhysicalPiece) extends FontHelpers {

  import GameOverPanel._


  class GameOverPanelBackend($: BackendScope[Props, Unit]) {


    private def handleClick(event: ReactEventI): Callback = {
      $.props.flatMap(props => props.applicationCallbacks.onNewGameButtonClicked)
    }

    private def backdrop(props: Props) = {
      <.svg.rect(
        ^.key := "backdrop",
        ^.`class` := s"game-over-panel-backdrop",
        ^.svg.x := 0,
        ^.svg.y := 0,
        ^.svg.width := props.widthPixels,
        ^.svg.height := props.heightPixels,
        ^.svg.rx := 20,
        ^.svg.ry := 20,
        ^.onMouseDown ==> handleClick
      )
    }

    private def textLine(x: Int, y: Int, textHeight: String, caption: String) = {
      <.svg.text(
        ^.svg.x := x,
        ^.svg.y := y,
        fontSize := textHeight,
        caption
      )
    }

    private def piece(key: String, x: Int, y: Int, size: Double, piece: Occupant) = {
      val pieceProps = PhysicalPieceProps.default.copy(
        piece = piece,
        x = x,
        y = y,
        scale = size
      )
      physicalPiece.component.withKey(key)(pieceProps)
    }

    private def pieceRow(centerX: Int, centerY: Int, pieceSize: Int, pieces: Vector[Occupant]) = {
      val left = centerX - 3 * pieceSize / 2
      val scaledSize = 0.95 * pieceSize
      val items = new js.Array[ReactNode]
      (0 to 3).foreach { index =>
        val pieceType = pieces(index)
        val x = left + index * pieceSize
        val item = piece(index.toString, x, centerY, scaledSize, pieceType)
        items.push(item)
      }
      <.svg.g(
        items
      )
    }


    def render(props: Props): ReactElement = {
      val centerX = props.widthPixels / 2
      val height = props.heightPixels
      val textHeight = {
        val h = height / 8
        s"${h}px"
      }
      val pieceSize = height / 3
      val pieceRowY = 11 * height / 24

      val parts = new js.Array[ReactNode]
      parts.push(backdrop(props))
      parts.push(textLine(centerX, 2 * height / 9, textHeight, "GAME OVER"))
      props.gameOverState match {
        case GameOverState.Winner(side, player) =>
          val pieces = if (side == DARK) darkWinPieces else lightWinPieces
          parts.push(pieceRow(centerX, pieceRowY, pieceSize, pieces))
          parts.push(textLine(centerX, 37 * height / 48, textHeight, player.displayName))
          parts.push(textLine(centerX, 11 * height / 12, textHeight, "WINS"))
        case GameOverState.Draw =>
          parts.push(pieceRow(centerX, pieceRowY, pieceSize, drawPieces))
          parts.push(textLine(centerX, 19 * height / 24, textHeight, "DRAW"))
      }
      <.svg.g(
        ^.`class` := s"game-over-panel",
        parts
      )
    }

  }

  val component = ReactComponentB[Props]("GameOverPanel")
    .renderBackend[GameOverPanelBackend]
    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, _) =>
      val result = scope.props != nextProps
      CallbackTo.pure(result)
    }
    .build


}