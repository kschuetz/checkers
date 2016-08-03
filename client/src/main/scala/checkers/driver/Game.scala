package checkers.driver

import checkers.components._
import checkers.core.GameDriver
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

  object Callbacks extends PieceCallbacks with BoardCallbacks {
    override val onPieceMouseDown = (event: PieceMouseEvent) => Some(Callback {
      println(s"pieceMouseDown ${event.tag}")
      gameDriver.handlePieceMouseDown(model, event).foreach(replaceModel)
    })

    override val onSquareMouseDown = (event: SquareMouseEvent) => Some(Callback {
      println(s"squareMouseDown ${event.squareIndex}")
    })

    override val onBoardMouseDown = (event: BoardMouseEvent) => Some(Callback {
      println(s"boardMouseDown ${event.boardPoint}")
    })
  }

  private def invalidate(): Unit = {
    dom.window.requestAnimationFrame(handleAnimationFrame _)
  }

  private def handleAnimationFrame(t: Double) = {
    model = model.updateNowTime(t)
    renderModel(model)
    if(model.hasActiveAnimations) invalidate()
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


}