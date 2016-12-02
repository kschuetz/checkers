package checkers.modules

import com.softwaremill.macwire.wire
import checkers.userinterface.GameScreen

trait UserInterfaceModule {
  lazy val gameScreen: GameScreen = wire[GameScreen]
}