package checkers.core

import checkers.userinterface._
import checkers.logger
import japgolly.scalajs.react.{Callback, ReactDOM}
import org.scalajs.dom
import org.scalajs.dom.window.performance


class Game(gameDriver: GameDriver,
           scheduler: Scheduler,
           applicationSettingsProvider: ApplicationSettingsProvider,
           screenLayoutSettingsProvider: ScreenLayoutSettingsProvider,
           gameScreen: GameScreen)
          (val host: dom.Node) {
  type Model = GameModel

  private var _running = false
  private var lastRenderTime = performance.now()
  private var lastHumanActivity = lastRenderTime
  private var clockTimeoutHandle: Int = 0
  private var lastClockDisplayHash: Int = 0

  private val applicationSettings = applicationSettingsProvider.applicationSettings
  private val clockUpdateInterval = applicationSettings.ClockUpdateIntervalMillis
  private val halfClockInterval = clockUpdateInterval / 2
  private val powerSaveIdleThreshold = applicationSettings.PowerSaveIdleThresholdSeconds.map(_ * 1000)

  private def stopped = !_running

  private var model: Model = {
    val nowTime = performance.now()
    gameDriver.createInitialModel(nowTime)
  }

  private var applicationCallbacks: ApplicationCallbacks = EmptyApplicationCallbacks

  def initApplicationCallbacks(value: ApplicationCallbacks): Unit = {
    applicationCallbacks = value
  }

  def run(): Unit = {
    _running = true
    invalidate()
  }

  def stop(): Unit = {
    _running = false
    ReactDOM.unmountComponentAtNode(host)
  }

  def rotateBoard(): Unit = {
    if(stopped) return
    updateNowTime()
    gameDriver.rotateBoard(model).foreach(userReplaceModel)
  }

  def rushComputer(): Unit = {
    humanActivity()
    gameDriver.rushComputer(model).foreach(userReplaceModel)
  }

  def humanActivity(): Unit = {
    lastHumanActivity = performance.now()
  }

  object Callbacks extends BoardCallbacks {
    override val onBoardMouseDown: BoardMouseEvent => Option[Callback] = (event: BoardMouseEvent) => Some(Callback {
      logger.inputEvents.info(s"pieceMouseDown ${event.squareIndex}")
      updateNowTime()
      gameDriver.handleBoardMouseDown(model, event).foreach(userReplaceModel)
      if (event.squareIndex < 0) {
        logger.inputEvents.debug(model.inputPhase.toString)
        scheduleTick()
      }
    })

    override val onBoardMouseMove: BoardMouseEvent => Option[Callback] = (event: BoardMouseEvent) => Some(Callback {
      updateNowTime()
      gameDriver.handleBoardMouseMove(model, event).foreach(userReplaceModel)
    })
  }

  private def invalidate(): Unit = {
    scheduleTick()
    dom.window.requestAnimationFrame(handleAnimationFrame _)
  }

  private def handleAnimationFrame(t: Double) = {
    model = model.updateNowTime(t)
    if (model.waitingForAnimations) {
      if (!model.hasActivePlayAnimations) {
        gameDriver.handleAnimationsComplete(model).foreach { newModel =>
          model = newModel
        }
      }
    }
    lastRenderTime = t
    lastClockDisplayHash = model.clockDisplayHash
    renderModel(model)
    if(clockTimeoutHandle == 0) scheduleClockTick(clockUpdateInterval)
    if (model.hasActiveAnimations || model.hasActiveComputation) invalidate()
  }

  private def renderModel(model: Model): Unit = {
    val props = GameScreen.Props(model, screenLayoutSettingsProvider.getScreenLayoutSettings, Callbacks, applicationCallbacks)
    val screen = gameScreen.create(props)
    ReactDOM.render(screen, host)
  }

  private def userReplaceModel(newModel: Model): Unit = {
    humanActivity()
    model = newModel
    invalidate()
  }

  private def computerReplaceModel(newModel: Model): Unit = {
    model = newModel
    invalidate()
  }


  private def tick(): Unit = {
    if (stopped) return
    updateNowTime()
    if (model.hasActiveComputation) {
      scheduler.executeSlice(model)
      gameDriver.processComputerMoves(model).foreach { newModel =>
        computerReplaceModel(newModel)
      }
      if (model.hasActiveComputation) {
        scheduleTick()
      }
    }
  }

  private def scheduleTick(): Unit = {
    if (stopped) return
    dom.window.setTimeout(tick _, 1)
  }

  private def updateNowTime(): Unit = {
    val t = performance.now()
    lastRenderTime = t
    model = model.updateNowTime(t)
  }

  private def scheduleClockTick(millis: Double): Unit = {
    cancelClockTick()
    clockTimeoutHandle = dom.window.setTimeout(clockTick _, millis)
  }

  private def cancelClockTick(): Unit = {
    if(clockTimeoutHandle != 0) dom.window.clearTimeout(clockTimeoutHandle)
    clockTimeoutHandle = 0
  }

  private def clockTick(): Unit = {
    clockTimeoutHandle = 0
    if(stopped) return

    val t = performance.now()
    val timeSinceLastRender = t - lastRenderTime

    if(model.inputPhase.waitingForHuman) {
      val timeSinceLastActivity = t - lastHumanActivity
      if(powerSaveIdleThreshold.exists(_ < timeSinceLastActivity)) return
    }

    if(timeSinceLastRender < halfClockInterval) {
      scheduleClockTick(clockUpdateInterval - timeSinceLastRender)
    } else {
      updateNowTime()

      // optimization to avoid calling renderModel if clock display hasn't changed
      val hash = model.clockDisplayHash
      if(hash != lastClockDisplayHash) {
        invalidate()
      } else {
        scheduleClockTick(clockUpdateInterval)
      }
    }
  }

}