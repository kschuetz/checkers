package checkers.core

import japgolly.scalajs.react.Callback

trait ApplicationCallbacks {
  def onNewGameButtonClicked: Callback
}


object EmptyApplicationCallbacks extends ApplicationCallbacks {
  val onNewGameButtonClicked: Callback = Callback.empty
}
