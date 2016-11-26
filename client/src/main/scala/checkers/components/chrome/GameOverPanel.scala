package checkers.components.chrome

import checkers.components.SceneFrame
import checkers.components.mixins.FontSize
import checkers.components.piece.{PhysicalPiece, PhysicalPieceProps}
import checkers.consts._
import checkers.core.{GameModelReader, GameOverState}
import checkers.util.CssHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.raw.SVGSVGElement

import scala.scalajs.js

object GameOverPanel extends FontSize {

  private val darkWinPieces = Vector(DARKKING, DARKKING, DARKKING, DARKKING)
  private val lightWinPieces = Vector(LIGHTKING, LIGHTKING, LIGHTKING, LIGHTKING)
  private val drawPieces = Vector(DARKMAN, LIGHTMAN, DARKMAN, LIGHTMAN)


  case class Props(widthPixels: Int,
                   heightPixels: Int,
                   gameOverState: GameOverState)

  class GameOverPanelBackend($: BackendScope[Props, Unit]) {

    private def backdrop(props: Props) = {
      <.svg.rect(
        ^.key := "backdrop",
        ^.`class` := s"game-over-panel-backdrop",
        ^.svg.x := 0,
        ^.svg.y := 0,
        ^.svg.width := props.widthPixels,
        ^.svg.height := props.heightPixels
      )
    }


    private def textLine(x: Int, y: Int, textHeight: String, caption: String) = {
      <.svg.text(
        ^.svg.x := x,
        ^.svg.y := y,
        ^.svg.textAnchor := "middle",
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
      PhysicalPiece.apply.withKey(key)(pieceProps)
    }

    private def pieceRow(centerX: Int, centerY: Int, pieceSize: Int, pieces: Vector[Occupant]) = {
      val left = centerX - 3 * pieceSize / 2
      val scaledSize = 0.9 * pieceSize
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


    def render(props: Props) = {
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
        case GameOverState.Winner(color, player) =>
          val pieces = if(color == DARK) darkWinPieces else lightWinPieces
          parts.push(pieceRow(centerX, pieceRowY, pieceSize, pieces))
          parts.push(textLine(centerX, 19 * height / 24, textHeight, player.displayName))
          parts.push(textLine(centerX, 11 * height / 12, textHeight, "WINS"))
        case GameOverState.Draw =>
          parts.push(pieceRow(centerX, pieceRowY, pieceSize, drawPieces))
          parts.push(textLine(centerX, 19 * height / 24, textHeight, "DRAW"))
      }
      <.svg.g(
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

  def apply(props: Props) = component(props)


}