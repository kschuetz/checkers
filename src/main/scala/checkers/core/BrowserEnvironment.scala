package checkers.core

trait ViewPortInfo {
  def viewPortWidth: Double
  def viewPortHeight: Double
}

case class BrowserEnvironment(viewPortWidth: Double,
                              viewPortHeight: Double) extends ViewPortInfo