package checkers.userinterface.chrome

import checkers.consts._
import checkers.core.{ApplicationCallbacks, GameOverState}
import checkers.userinterface.mixins.FontHelpers
import checkers.userinterface.piece.{PhysicalPiece, PhysicalPieceProps}
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{svg_<^ => svg}

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

  class Backend($: BackendScope[Props, Unit]) {

    private def handleClick(event: ReactEventFromInput): Callback = {
      $.props.flatMap(props => props.applicationCallbacks.onNewGameButtonClicked)
    }

    private def backdrop(props: Props) = {
      svg.<.rect(
        ^.key := "backdrop",
        ^.`class` := s"game-over-panel-backdrop",
        svg.^.x := 0.asInstanceOf[JsNumber],
        svg.^.y := 0.asInstanceOf[JsNumber],
        svg.^.width := props.widthPixels.asInstanceOf[JsNumber],
        svg.^.height := props.heightPixels.asInstanceOf[JsNumber],
        svg.^.rx := 20.asInstanceOf[JsNumber],
        svg.^.ry := 20.asInstanceOf[JsNumber],
        ^.onMouseDown ==> handleClick
      )
    }

    private def textLine(x: Int, y: Int, textHeight: String, caption: String, key: String) = {
      svg.<.text(
        ^.key := key,
        svg.^.x := x.asInstanceOf[JsNumber],
        svg.^.y := y.asInstanceOf[JsNumber],
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
      physicalPiece.create.withKey(key)(pieceProps)
    }

    private def pieceRow(centerX: Int, centerY: Int, pieceSize: Int, pieces: Vector[Occupant]) = {
      val left = centerX - 3 * pieceSize / 2
      val scaledSize = 0.95 * pieceSize
      val items = VdomArray.empty()
      (0 to 3).foreach { index =>
        val pieceType = pieces(index)
        val x = left + index * pieceSize
        val item = piece(index.toString, x, centerY, scaledSize, pieceType)
        items += item
      }
      svg.<.g(
        ^.key := "piece-row",
        items
      )
    }


    def render(props: Props): VdomElement = {
      val centerX = props.widthPixels / 2
      val height = props.heightPixels
      val textHeight = {
        val h = height / 8
        s"${h}px"
      }
      val pieceSize = height / 3
      val pieceRowY = 11 * height / 24

      val parts = VdomArray.empty()
      parts += backdrop(props)
      parts += textLine(centerX, 2 * height / 9, textHeight, "GAME OVER", "t1")
      props.gameOverState match {
        case GameOverState.Winner(side, player) =>
          val pieces = if (side == DARK) darkWinPieces else lightWinPieces
          parts += pieceRow(centerX, pieceRowY, pieceSize, pieces)
          parts += textLine(centerX, 37 * height / 48, textHeight, player.displayName, "t2")
          parts += textLine(centerX, 11 * height / 12, textHeight, "WINS", "t3")
        case GameOverState.Draw =>
          parts += pieceRow(centerX, pieceRowY, pieceSize, drawPieces)
          parts += textLine(centerX, 19 * height / 24, textHeight, "DRAW", "t4")
      }
      svg.<.g(
        ^.`class` := s"game-over-panel",
        parts
      )
    }

  }

  val create = ScalaComponent.builder[Props]("GameOverPanel")
    .renderBackend[Backend]
    .shouldComponentUpdate { x => CallbackTo.pure(x.cmpProps(_ != _)) }
    .build


}
