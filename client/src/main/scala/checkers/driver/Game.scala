package checkers.driver

import checkers.components.GameScreen
import checkers.components.piece.{PieceCallbacks, PieceMouseEvent}
import checkers.core.{GameDriver, GameLogicModule}
import checkers.models.GameModel
import japgolly.scalajs.react.{Callback, ReactDOM}
import org.scalajs.dom
import org.scalajs.dom.window.performance


class Game[DS, LS](gameLogicModule: GameLogicModule,
                   gameDriver: GameDriver[DS, LS])
                  (val host: dom.Node) {
  type Model = GameModel[DS, LS]

  protected val moveGenerator = gameLogicModule.moveGenerator
  protected val moveExecutor = gameLogicModule.moveExecutor
  protected val moveTreeFactory = gameLogicModule.moveTreeFactory

  var model: Model = {
    val nowTime = performance.now()
    gameDriver.createInitialModel(nowTime)
  }
//    .copy(clickableSquares = (0 to 31).toSet,
//      ghostPiece = Some(GhostPiece(DARKMAN, 21, Point(-0.15, -0.13), Point(1.0, 1.0))))

  object Callbacks extends PieceCallbacks {
    override val onPieceMouseDown = (event: PieceMouseEvent) => Some(Callback {
      println("in handlePieceMouseDown")
      println(s"(${event.reactEvent.clientX}, ${event.reactEvent.clientY})")
      println(event)
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

  def tick(): Unit = {
    import checkers.core.InputPhase._
    model.inputPhase match {
      case GameStart => startGame(model)
      case _ => ()
    }
  }

  private def startGame(model: Model): Unit = {
  }

  private def beginTurn(model: Model): Unit = {
  }

  private def endTurn(model: Model): Unit = {

  }


  private def replaceModel(newModel: Model): Unit = {
    model = newModel
    invalidate()
  }

//  private def nextTurn(model: GameModel[DS, LS]): GameModel[DS, LS] = {
//
//  }

}