package checkers.userinterface.gamelog

import checkers.core.{GameModelReader, Notation, Play, Snapshot}
import checkers.userinterface.mixins.ClipPathHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object GameLogDisplay {

  case class Props(upperLeftX: Double,
                   upperLeftY: Double,
                   widthPixels: Double,
                   heightPixels: Double,
                   entryHeightPixels: Double,
                   gameModel: GameModelReader)

}

class GameLogDisplay(notation: Notation,
                     gameLogEntry: GameLogEntry) extends ClipPathHelpers {

  import GameLogDisplay._

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props): ReactElement = {
      val gameModel = props.gameModel
      val entryHeight = props.entryHeightPixels
      val clientHeight = props.heightPixels
      val entryLeftX = 0
      val entryWidth = props.widthPixels

      val entries = new js.Array[ReactNode]

      var y = 0d

      def addEntryPanel(snapshot: Snapshot, play: Option[Play]): Unit = {
        var moveDescription = for {
          p <- play
          desc <- notation.notationForPlay(p)
        } yield desc

        val key = s"${snapshot.turnIndex}"
        val entryPanelProps = GameLogEntry.Props(
          widthPixels = entryWidth,
          heightPixels = entryHeight,
          turnIndex = snapshot.turnIndex + 1,
          side = snapshot.turnToMove,
          moveDescription = moveDescription,
          upperLeftX = entryLeftX,
          upperLeftY = y)

        val element = gameLogEntry.create.withKey(key)(entryPanelProps)
        entries.push(element)

        y += entryHeight
      }

      if(gameModel.inputPhase.waitingForMove) {
        addEntryPanel(gameModel.currentTurnSnapshot, None)
      }
      var historyEntries = gameModel.history

      while (y < clientHeight && historyEntries.nonEmpty) {
        val historyEntry :: next = historyEntries
        addEntryPanel(historyEntry.snapshot, Some(historyEntry.play))
        historyEntries = next
      }

      val backdrop = <.svg.rect(
        ^.`class` := "game-log-backdrop",
        ^.svg.x := 0,
        ^.svg.y := 0,
        ^.svg.width := props.widthPixels,
        ^.svg.height := props.heightPixels
      )

      val transform = s"translate(${props.upperLeftX},${props.upperLeftY})"

      <.svg.g(
        ^.`class` := "game-log-display",
        ^.svg.transform := transform,
        backdrop,
        entries
      )

    }
  }

  val create = ReactComponentB[Props]("GameLogDisplay")
    .renderBackend[Backend]
    .build
}