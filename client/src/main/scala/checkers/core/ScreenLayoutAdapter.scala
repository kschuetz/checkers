package checkers.core

class ScreenLayoutAdapter extends ScreenLayoutSettingsProvider {

  private case class CacheSettings(width: Int,
                                   height: Int,
                                   settings: ScreenLayoutSettings)

  private var cache: CacheSettings = _

  private lazy val largeScreen: ScreenLayoutSettings = new DefaultScreenLayoutSettings {}

  override def getScreenLayoutSettings(viewPortInfo: ViewPortInfo): ScreenLayoutSettings = {
    val width = viewPortInfo.viewPortWidth.toInt
    val height = viewPortInfo.viewPortHeight.toInt

    if(cache != null && cache.width == width && cache.height == height) cache.settings
    else {
      val newSettings = largeScreen  // TODO

      println(s"ScreenLayoutAdapter:  width = $width, height = $height")

      cache = CacheSettings(width, height, newSettings)
      newSettings
    }
  }
}