package checkers.driver

import checkers.components.GameScreen
import checkers.models.GameScreenModel
import japgolly.scalajs.react.ReactDOM
import org.scalajs.dom

class GameScreenDriver(val host: dom.Node,
                       initialModel: GameScreenModel) {
  var model = initialModel

  private def invalidate(): Unit = {
    dom.window.requestAnimationFrame(handleAnimationFrame _)
  }

  private def handleAnimationFrame(t: Double) = {
    model = model.updateNowTime(t)
    renderModel(model)
    if(model.hasActiveAnimations) invalidate()
  }

  private def renderModel(model: GameScreenModel): Unit = {
    val screen = GameScreen.apply(model)
    ReactDOM.render(screen, host)
  }

  def run(): Unit = {
    invalidate()
  }



}