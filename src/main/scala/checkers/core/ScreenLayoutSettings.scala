package checkers.core

trait GameSceneHeight {
  def GameSceneHeightPixels: Int
}

trait GameLogLayoutSettings {
  def GameLogPaddingPixelsX: Int

  def GameLogPaddingPixelsY: Int

  def GameLogEntryHeightPixels: Int

  def GameLogScrollButtonHeightPixels: Int
}

trait SideChromeLayoutSettings extends GameSceneHeight with GameLogLayoutSettings {
  def SideChromeWidthPixels: Int

  def SideChromePaddingPixels: Int

  def SideChromeButtonPaddingPixelsX: Int

  def SideChromeButtonPaddingPixelsY: Int

  def SideChromeButtonAreaPaddingY: Int

  def SideChromeButtonHeightPixels: Int

  def SideChromeDrawCountdownIndicatorHeightPixels: Int

  def SideChromeDrawCountdownIndicatorWidthPixels: Int

  def SideChromePowerMeterHeightPixels: Int

  def SideChromePowerMeterWidthPixels: Int
}

trait GameOverPanelLayoutSettings {
  def GameOverPanelWidthPixels: Int

  def GameOverPanelHeightPixels: Int
}

trait ScreenLayoutSettings
  extends SideChromeLayoutSettings
    with GameOverPanelLayoutSettings {

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

trait DefaultScreenLayoutSettings extends ScreenLayoutSettings {

  override def GameSceneWidthPixels: Int = 800

  override def GameSceneHeightPixels: Int = 800

  override def TopChromeHeightPixels: Int = 90

  override def SideChromeWidthPixels: Int = 200

  override def TopChromePaddingPixels: Int = 6

  override def SideChromePaddingPixels: Int = 6

  override def SideChromeButtonPaddingPixelsX: Int = 10

  override def SideChromeButtonPaddingPixelsY: Int = 12

  override def SideChromeButtonHeightPixels: Int = 48

  override def SideChromeButtonAreaPaddingY: Int = 14

  override def SideChromePowerMeterHeightPixels: Int = 30

  override def SideChromePowerMeterWidthPixels: Int = 170

  override def SideChromeDrawCountdownIndicatorHeightPixels: Int = 30

  override def SideChromeDrawCountdownIndicatorWidthPixels: Int = 170

  override def GameOverPanelWidthPixels: Int = 400

  override def GameOverPanelHeightPixels: Int = 200

  override def GameLogPaddingPixelsX: Int = 10

  override def GameLogPaddingPixelsY: Int = 10

  override def GameLogEntryHeightPixels: Int = 40

  override def GameLogScrollButtonHeightPixels: Int = 20


}