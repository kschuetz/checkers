package checkers.core

import japgolly.scalajs.react.Callback

trait ApplicationCallbacks {
  def onNewGameButtonClicked: Callback
  def onRotateBoardButtonClicked: Callback
  def onRushButtonClicked: Callback
  def onHintButtonClicked: Callback
}

object EmptyApplicationCallbacks extends ApplicationCallbacks {
  val onNewGameButtonClicked: Callback = Callback.empty
  val onRotateBoardButtonClicked: Callback = Callback.empty
  val onRushButtonClicked: Callback = Callback.empty
  val onHintButtonClicked: Callback = Callback.empty
}
