package checkers.core

trait ScreenLayoutSettings {
  def GameSceneWidthPixels: Int
  def GameSceneHeightPixels: Int
  def TopChromeHeightPixels: Int
  def SideChromeWidthPixels: Int
  def ChromePaddingPixels: Int
}


object DefaultScreenLayoutSettings extends ScreenLayoutSettings {
  override def GameSceneWidthPixels: Int = 800

  override def GameSceneHeightPixels: Int = 800

  override def TopChromeHeightPixels: Int = 90

  override def SideChromeWidthPixels: Int = 90

  override def ChromePaddingPixels: Int = 6
}