package checkers.driver

import checkers.components.GameScreen
import checkers.components.piece.{PieceCallbacks, PieceMouseEvent}
import checkers.core.DarkMan
import checkers.geometry.Point
import checkers.models.{GameScreenModel, GhostPiece}
import japgolly.scalajs.react.{Callback, ReactDOM}
import org.scalajs.dom

class GameScreenDriver(val host: dom.Node,
                       initialModel: GameScreenModel) {
  var model = initialModel
    .copy(clickableSquares = (0 to 31).toSet,
      ghostPiece = Some(GhostPiece(DarkMan, 21, Point(-0.15, -0.13), Point(1.0, 1.0))))



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

  private def renderModel(model: GameScreenModel): Unit = {
    val screen = GameScreen.apply((model, Callbacks))
    ReactDOM.render(screen, host)
  }

  def run(): Unit = {
    invalidate()
  }



}