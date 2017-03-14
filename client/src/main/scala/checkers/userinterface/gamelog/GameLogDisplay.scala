package checkers.userinterface.gamelog

import checkers.core._
import checkers.userinterface.mixins.ClipPathHelpers
import checkers.userinterface.widgets.ScrollButton
import checkers.util.SvgHelpers
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{svg_<^ => svg}

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

  case class State(scrollOffset: Int,
                   scrolledDown: Boolean) {
    def shouldUpdate(other: State): Boolean = this != other
  }

  private val defaultState = State(0, scrolledDown = false)

  private val bodyClipPathId = "game-log-display-clip-path"
}

class GameLogDisplay(notation: Notation,
                     gameLogEntry: GameLogEntry,
                     scrollButton: ScrollButton) extends ClipPathHelpers with SvgHelpers {

  import GameLogDisplay._

  class Backend($: BackendScope[Props, State]) {

    def render(props: Props, state: State): VdomElement = {
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

      var scrollUpNextOffset = 0
      var scrollDownNextOffset = 0
      var skipEntries = 0

      var scrollUpEnabled = false
      var scrollDownEnabled = false

      val offsetPixels = if(totalHeightNeeded > clientHeight) {
        val scrollOffset = state.scrollOffset
        val maxOffset = (totalHeightNeeded - clientHeight) / entryHeight
        val maxOffsetI = maxOffset.toInt

        val result = if(!state.scrolledDown) {
          skipEntries = scrollOffset
          0d
        } else {
          val partialEntry = 1 - (maxOffset - maxOffsetI)
          if(partialEntry > 0.1) {
            skipEntries = scrollOffset
            partialEntry * entryHeight
          } else {
            skipEntries = scrollOffset
            0d
          }
        }

        skipEntries = math.max(0, math.min(skipEntries, maxOffsetI))

        scrollUpEnabled = scrollOffset > 0

        scrollDownNextOffset = math.min(scrollOffset + 1, maxOffsetI)
        scrollUpNextOffset = math.max(scrollOffset - 1, 0)

        result
      } else 0d

      val entries = new js.Array[VdomNode]

      var y = clientTop - offsetPixels

      def addEntryPanel(snapshot: Snapshot, play: Option[Play]): Unit = {
        if(skipEntries > 0) {
          skipEntries -= 1
        } else {
          val moveDescription = for {
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

      val backdrop = svg.<.rect(
        ^.`class` := "game-log-backdrop",
        svg.^.x := 0.asInstanceOf[JsNumber],
        svg.^.y := 0.asInstanceOf[JsNumber],
        svg.^.width := props.widthPixels.asInstanceOf[JsNumber],
        svg.^.height := props.heightPixels.asInstanceOf[JsNumber]
      )

      val transform = s"translate(${props.upperLeftX},${props.upperLeftY})"

      val bodyClipPath = svg.<.defs(
        svg.<.clipPathTag(
          ^.id := bodyClipPathId,
          svg.<.rect(
            svg.^.x := 0.asInstanceOf[JsNumber],
            svg.^.y := clientTop.asInstanceOf[JsNumber],
            svg.^.width := props.widthPixels.asInstanceOf[JsNumber],
            svg.^.height := clientHeight.asInstanceOf[JsNumber]
          )
        )
      )

      val logBody = svg.<.g(
        ^.`class` := "game-log-display",
        clipPathAttr := s"url(#$bodyClipPathId)",
        backdrop,
        entries.toVdomArray
      )

      val scrollUpButton: Option[VdomElement] = if(scrollUpEnabled) {
        val buttonProps = ScrollButton.Props(
          centerX = halfWidth,
          centerY = halfScrollButtonHeight,
          width = scrollButtonWidth,
          height = scrollButtonHeight,
          up = true,
          onClick = $.modState(_.copy(scrollOffset = scrollUpNextOffset, scrolledDown = false))
        )
        val button = scrollButton.create(buttonProps)
        Some(button)
      } else None

      val scrollDownButton: Option[VdomElement] = if(scrollDownEnabled) {
        val buttonProps = ScrollButton.Props(
          centerX = halfWidth,
          centerY = totalHeight - halfScrollButtonHeight,
          width = scrollButtonWidth,
          height = scrollButtonHeight,
          up = false,
          onClick = $.modState(_.copy(scrollOffset = scrollDownNextOffset, scrolledDown = true))
        )
        val button = scrollButton.create(buttonProps)
        Some(button)
      } else None

      svg.<.g(
        svg.^.transform := transform,
        bodyClipPath,
        logBody,
        scrollUpButton.whenDefined,
        scrollDownButton.whenDefined
      )

    }
  }

  val create = ScalaComponent.build[Props]("GameLogDisplay")
    .initialState[State](defaultState)
    .renderBackend[Backend]
      .shouldComponentUpdate { x =>
        val result = x.currentProps.shouldUpdate(x.nextProps) || x.currentState.shouldUpdate(x.nextState)
        CallbackTo.pure(result)
      }
    .build
}