package checkers.core

import checkers.components._
import checkers.models.GameModel
import japgolly.scalajs.react.{Callback, ReactDOM}
import org.scalajs.dom
import org.scalajs.dom.window.performance


class Game[DS, LS](gameDriver: GameDriver[DS, LS])
                  (val host: dom.Node) {
  type Model = GameModel[DS, LS]


  var model: Model = {
    val nowTime = performance.now()
    gameDriver.createInitialModel(nowTime)
  }

  object Callbacks extends BoardCallbacks {
    override val onBoardMouseDown = (event: BoardMouseEvent) => Some(Callback {
      println(s"pieceMouseDown ${event.squareIndex}")
      updateNowTime()
      gameDriver.handleBoardMouseDown(model, event).foreach(replaceModel)
      if(event.squareIndex < 0) {
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
    if(model.waitingForAnimations) {
      if(!model.hasActivePlayAnimations) {
        gameDriver.handleAnimationsComplete(model).foreach { newModel =>
          model = newModel
        }
      }
    }
    renderModel(model)
    if (model.hasActiveAnimations || model.hasActiveComputation) invalidate()
  }

  private def renderModel(model: Model): Unit = {
    val screen = GameScreen.apply((model, Callbacks))
    ReactDOM.render(screen, host)
  }

  def run(): Unit = {
    invalidate()
  }

  private def replaceModel(newModel: Model): Unit = {
    model = newModel
    invalidate()
  }


  private def tick(): Unit = {
    updateNowTime()
    if(model.hasActiveComputation) {
      model.runComputations(2000)
      gameDriver.processComputerMoves(model).foreach { newModel =>
        replaceModel(newModel)
      }
      if(model.hasActiveComputation) {
        scheduleTick()
      }
    }
  }

  private def scheduleTick(): Unit = {
    dom.window.setTimeout(tick _, 1)
  }

  private def updateNowTime(): Unit = {
    val t = performance.now()
    model = model.updateNowTime(t)
  }

}