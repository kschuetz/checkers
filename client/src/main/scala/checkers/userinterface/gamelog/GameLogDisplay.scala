package checkers.userinterface.gamelog

import checkers.core._
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
                   scrollButtonHeightPixels: Double,
                   updateId: Int,
                   waitingForMove: Boolean,
                   currentSnapshot: Snapshot,
                   history: List[HistoryEntry]) {

    def shouldUpdate(other: Props): Boolean = {
      (updateId != other.updateId) ||
        (waitingForMove != other.waitingForMove) ||
        (upperLeftX != other.upperLeftX) ||
        (upperLeftY != other.upperLeftY) ||
        (widthPixels != other.widthPixels) ||
        (heightPixels != other.heightPixels) ||
        (entryHeightPixels != other.entryHeightPixels) ||
        (scrollButtonHeightPixels != other.scrollButtonHeightPixels)
    }
  }

  case class State(scrollOffset: Int,
                   scrolledDown: Boolean) {
    def shouldUpdate(other: State): Boolean = this != other
  }

  private val defaultState = State(0, scrolledDown = false)

  private val bodyClipPathId = "game-log-display-clip-path"
}

class GameLogDisplay(notation: Notation,
                     gameLogEntry: GameLogEntry) extends ClipPathHelpers {

  import GameLogDisplay._

  class Backend($: BackendScope[Props, State]) {
    def render(props: Props, state: State): ReactElement = {
      val entryHeight = props.entryHeightPixels
      val scrollButtonHeight = props.scrollButtonHeightPixels + 3
      val clientTop: Double = scrollButtonHeight
      val clientHeight = props.heightPixels - 2 * scrollButtonHeight
      val entryLeftX = 0
      val entryWidth = props.widthPixels

      val entries = new js.Array[ReactNode]

      var y = clientTop

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

      if(props.waitingForMove) {
        addEntryPanel(props.currentSnapshot, None)
      }
      var historyEntries = props.history

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

      val bodyClipPath = <.svg.defs(
        <.svg.clipPathTag(
          ^.id := bodyClipPathId,
          <.svg.rect(
            ^.svg.x := 0,
            ^.svg.y := clientTop,
            ^.svg.width := props.widthPixels,
            ^.svg.height := clientHeight
          )
        )
      )

      val logBody = <.svg.g(
        ^.`class` := "game-log-display",
        clipPathAttr := s"url(#$bodyClipPathId)",
        backdrop,
        entries
      )

      <.svg.g(
        ^.svg.transform := transform,
        bodyClipPath,
        logBody
      )

    }
  }

  val create = ReactComponentB[Props]("GameLogDisplay")
    .initialState[State](defaultState)
    .renderBackend[Backend]
    .shouldComponentUpdateCB { case ShouldComponentUpdate(scope, nextProps, nextState) =>
      val result = scope.props.shouldUpdate(nextProps) || scope.state.shouldUpdate(nextState)
      CallbackTo.pure(result)
    }
    .build
}