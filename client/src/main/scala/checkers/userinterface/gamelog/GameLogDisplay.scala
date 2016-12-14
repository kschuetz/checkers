package checkers.userinterface.gamelog

import checkers.core._
import checkers.userinterface.mixins.ClipPathHelpers
import checkers.userinterface.widgets.ScrollButton
import checkers.util.SvgHelpers
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
                   history: Vector[HistoryEntry]) {

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

  case class State(scrollOffset: Double,
                   scrolledUp: Boolean) {
    def shouldUpdate(other: State): Boolean = this != other
  }

  private val defaultState = State(0d, scrolledUp = false)

  private val bodyClipPathId = "game-log-display-clip-path"
}

class GameLogDisplay(notation: Notation,
                     gameLogEntry: GameLogEntry,
                     scrollButton: ScrollButton) extends ClipPathHelpers with SvgHelpers {

  import GameLogDisplay._

  class Backend($: BackendScope[Props, State]) {

    def render(props: Props, state: State): ReactElement = {
      val entryHeight = props.entryHeightPixels
      val scrollButtonWidth = 0.95 * props.widthPixels
      val scrollButtonHeight = props.scrollButtonHeightPixels
      val halfScrollButtonHeight = scrollButtonHeight / 2
      val scrollButtonHeightWidthPadding = scrollButtonHeight + 5
      val totalHeight = props.heightPixels
      val clientTop: Double = scrollButtonHeightWidthPadding
      val clientHeight = totalHeight - 2 * scrollButtonHeightWidthPadding
      val clientBottom = clientTop + clientHeight
      val entryLeftX = 0
      val entryWidth = props.widthPixels
      val halfWidth = entryWidth / 2

      val currentSnapshotCount = if(props.waitingForMove) 1 else 0

      val historyEntryCount = props.history.size
      val entryDisplayCount = historyEntryCount + currentSnapshotCount
      val totalHeightNeeded = entryDisplayCount * entryHeight

      var scrollUpNextOffset = 0d
      var scrollDownNextOffset = 0d
      var skipEntries = 0

      var scrollUpEnabled = false
      var scrollDownEnabled = false

      val offsetPixels = if(totalHeightNeeded > clientHeight) {
        val maxOffset = (totalHeightNeeded - clientHeight) / entryHeight
        val offset = math.min(state.scrollOffset, maxOffset)
        val offsetFloor = math.floor(offset)

        //println(s"state: ${state.scrollOffset}  max: $maxOffset")

        skipEntries = offsetFloor.toInt

        scrollUpEnabled = offset > 0

        scrollUpNextOffset = math.max(0d, if(offset - offsetFloor < 0.1) math.floor(offset - 1) else offsetFloor)

        //println(s"offset: $offset  skipEntries: $skipEntries")

        val partialEntry = offset - skipEntries

        val bottomCutoff = if(partialEntry > 0) 1 - partialEntry else 0d

        scrollDownNextOffset = math.min(maxOffset, if(bottomCutoff > 0.1) offset + bottomCutoff else offset + 1 + bottomCutoff)

        partialEntry * entryHeight
      } else 0d

      println(s"offsetPixels: $offsetPixels")

      val entries = new js.Array[ReactNode]

      var y = clientTop - offsetPixels

      def addEntryPanel(snapshot: Snapshot, play: Option[Play]): Unit = {
        if(skipEntries > 0) {
          skipEntries -= 1
        } else {
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
      }

      if(props.waitingForMove) {
        addEntryPanel(props.currentSnapshot, None)
      }
      var historyEntryIndex = historyEntryCount - 1

      while (y <= clientBottom && historyEntryIndex >= 0) {
        val historyEntry = props.history(historyEntryIndex)
        addEntryPanel(historyEntry.snapshot, Some(historyEntry.play))
        historyEntryIndex -= 1
      }

      scrollDownEnabled = y > clientBottom || historyEntryIndex >= 0

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

      val scrollUpButton: Option[ReactElement] = if(scrollUpEnabled) {
        val buttonProps = ScrollButton.Props(
          centerX = halfWidth,
          centerY = halfScrollButtonHeight,
          width = scrollButtonWidth,
          height = scrollButtonHeight,
          up = true,
          onClick = CallbackTo {
            println(s"scroll up to $scrollUpNextOffset")
            $.modState(_.copy(scrollOffset = scrollUpNextOffset, scrolledUp = true))
          }.flatten
        )
        val button = scrollButton.create(buttonProps)
        Some(button)
      } else None

      val scrollDownButton: Option[ReactElement] = if(scrollDownEnabled) {
        val buttonProps = ScrollButton.Props(
          centerX = halfWidth,
          centerY = totalHeight - halfScrollButtonHeight,
          width = scrollButtonWidth,
          height = scrollButtonHeight,
          up = false,
          onClick = CallbackTo {
            println(s"scroll down to $scrollDownNextOffset")
            $.modState(_.copy(scrollOffset = scrollDownNextOffset, scrolledUp = false))
          }.flatten
        )
        val button = scrollButton.create(buttonProps)
        Some(button)
      } else None

      <.svg.g(
        ^.svg.transform := transform,
        bodyClipPath,
        logBody,
        scrollUpButton,
        scrollDownButton
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