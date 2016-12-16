package checkers.core

import checkers.logger

class ScreenLayoutAdapter extends ScreenLayoutSettingsProvider {

  private case class CacheSettings(width: Int,
                                   height: Int,
                                   settings: ScreenLayoutSettings)

  private var cache: CacheSettings = _

  private val thresholds = Vector(
    (1024, 896),
    (914, 786),
    (804, 676),
    (694, 566))

  private lazy val levels = Vector(
    // large
    new DefaultScreenLayoutSettings { },

    new DefaultScreenLayoutSettings {
      override def GameSceneWidthPixels: Int = 700

      override def GameSceneHeightPixels: Int = 700

      override def TopChromeHeightPixels: Int = 80

      override def SideChromeWidthPixels: Int = 190

      override def SideChromeButtonHeightPixels: Int = 42

      override def GameLogEntryHeightPixels: Int = 35

      override def SideChromePowerMeterHeightPixels: Int = 28

      override def SideChromePowerMeterWidthPixels: Int = 172

      override def SideChromeDrawCountdownIndicatorHeightPixels: Int = 28

      override def SideChromeDrawCountdownIndicatorWidthPixels: Int = 172
    },

    new DefaultScreenLayoutSettings {
      override def GameSceneWidthPixels: Int = 600

      override def GameSceneHeightPixels: Int = 600

      override def TopChromeHeightPixels: Int = 70

      override def SideChromeWidthPixels: Int = 180

      override def SideChromeButtonHeightPixels: Int = 36

      override def GameLogEntryHeightPixels: Int = 30

      override def SideChromePowerMeterHeightPixels: Int = 26

      override def SideChromePowerMeterWidthPixels: Int = 162

      override def SideChromeDrawCountdownIndicatorHeightPixels: Int = 26

      override def SideChromeDrawCountdownIndicatorWidthPixels: Int = 162
    },

    new DefaultScreenLayoutSettings {
      override def GameSceneWidthPixels: Int = 500

      override def GameSceneHeightPixels: Int = 500

      override def TopChromeHeightPixels: Int = 60

      override def SideChromeWidthPixels: Int = 170

      override def SideChromeButtonHeightPixels: Int = 30

      override def GameLogEntryHeightPixels: Int = 25

      override def SideChromePowerMeterHeightPixels: Int = 23

      override def SideChromePowerMeterWidthPixels: Int = 152

      override def SideChromeDrawCountdownIndicatorHeightPixels: Int = 23

      override def SideChromeDrawCountdownIndicatorWidthPixels: Int = 152
    }
  ).lift

  private lazy val smallest = new DefaultScreenLayoutSettings {
    override def GameSceneWidthPixels: Int = 400

    override def GameSceneHeightPixels: Int = 400

    override def TopChromeHeightPixels: Int = 50

    override def SideChromeWidthPixels: Int = 160

    override def SideChromeButtonHeightPixels: Int = 24

    override def GameLogEntryHeightPixels: Int = 25

    override def SideChromePowerMeterHeightPixels: Int = 23

    override def SideChromePowerMeterWidthPixels: Int = 144

    override def SideChromeDrawCountdownIndicatorHeightPixels: Int = 23

    override def SideChromeDrawCountdownIndicatorWidthPixels: Int = 144
  }

  private def getSettings(width: Int, height: Int): ScreenLayoutSettings = {
    val idx = thresholds.indexWhere { case (minWidth, minHeight) =>
      width >= minWidth && height >= minHeight
    }
    levels(idx).getOrElse(smallest)
  }

  override def getScreenLayoutSettings(viewPortInfo: ViewPortInfo): ScreenLayoutSettings = {
    val width = viewPortInfo.viewPortWidth.toInt
    val height = viewPortInfo.viewPortHeight.toInt

    if(cache != null && cache.width == width && cache.height == height) cache.settings
    else {
      val newSettings = getSettings(width, height)

      logger.log.info(s"Adapting screen layout for dimensions ($width × $height)")

      cache = CacheSettings(width, height, newSettings)
      newSettings
    }
  }
}