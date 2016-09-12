package checkers.core

import checkers.components._
import japgolly.scalajs.react.{Callback, ReactDOM}
import org.scalajs.dom
import org.scalajs.dom.window.performance


class Game(gameDriver: GameDriver,
           screenLayoutSettingsProvider: ScreenLayoutSettingsProvider)
          (val host: dom.Node) {
  type Model = GameModel

  private var _running = false

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
    gameDriver.rotateBoard(model).foreach(replaceModel)
  }

  object Callbacks extends BoardCallbacks {
    override val onBoardMouseDown = (event: BoardMouseEvent) => Some(Callback {
      println(s"pieceMouseDown ${event.squareIndex}")
      updateNowTime()
      gameDriver.handleBoardMouseDown(model, event).foreach(replaceModel)
      if (event.squareIndex < 0) {
        println(model.inputPhase)
        scheduleTick()
      }
    })

    override val onBoardMouseMove = (event: BoardMouseEvent) => Some(Callback {
      updateNowTime()
      gameDriver.handleBoardMouseMove(model, event).foreach(replaceModel)
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
    renderModel(model)
    if (model.hasActiveAnimations || model.hasActiveComputation) invalidate()
  }

  private def renderModel(model: Model): Unit = {
    val props = GameScreen.Props(model, screenLayoutSettingsProvider.getScreenLayoutSettings, Callbacks, applicationCallbacks)
    val screen = GameScreen.apply(props)
    ReactDOM.render(screen, host)
  }



  private def replaceModel(newModel: Model): Unit = {
    model = newModel
    invalidate()
  }


  private def tick(): Unit = {
    if (stopped) return
    updateNowTime()
    if (model.hasActiveComputation) {
      model.runComputations(2000)
      gameDriver.processComputerMoves(model).foreach { newModel =>
        replaceModel(newModel)
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
    model = model.updateNowTime(t)
  }

}