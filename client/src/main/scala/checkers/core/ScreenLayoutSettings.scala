package checkers.core

trait GameSceneHeight {
  def GameSceneHeightPixels: Int
}

trait SideChromeLayoutSettings extends GameSceneHeight {
  def SideChromeWidthPixels: Int
  def SideChromePaddingPixels: Int
  def SideChromeButtonPaddingPixelsX: Int
  def SideChromeButtonPaddingPixelsY: Int
  def SideChromeButtonAreaPaddingY: Int
  def SideChromeButtonHeightPixels: Int
}

trait GameOverPanelLayoutSettings {
  def GameOverPanelWidthPixels: Int
  def GameOverPanelHeightPixels: Int
}

trait ScreenLayoutSettings extends SideChromeLayoutSettings with GameOverPanelLayoutSettings {
  def GameSceneWidthPixels: Int
  def TopChromeHeightPixels: Int
  def TopChromePaddingPixels: Int
}

trait ScreenLayoutSettingsProvider {
  def getScreenLayoutSettings(viewPortInfo: ViewPortInfo): ScreenLayoutSettings
}

case class ConstantScreenLayoutSettings(settings: ScreenLayoutSettings) extends ScreenLayoutSettingsProvider {
  def getScreenLayoutSettings(viewPortInfo: ViewPortInfo): ScreenLayoutSettings = settings
}

object DefaultScreenLayoutSettings extends ScreenLayoutSettings {

  override def GameSceneWidthPixels: Int = 800

  override def GameSceneHeightPixels: Int = 800

  override def TopChromeHeightPixels: Int = 90

  override def SideChromeWidthPixels: Int = 250

  override def TopChromePaddingPixels: Int = 6

  override def SideChromePaddingPixels: Int = 6

  override def SideChromeButtonPaddingPixelsX: Int = 10

  override def SideChromeButtonPaddingPixelsY: Int = 12

  override def SideChromeButtonHeightPixels: Int = 48

  override def SideChromeButtonAreaPaddingY: Int = 14

  override def GameOverPanelWidthPixels: Int = 400

  override def GameOverPanelHeightPixels: Int = 200
}