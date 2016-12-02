package checkers.modules

import com.softwaremill.macwire.wire
import checkers.userinterface.{GameScreen, SceneContainer}
import checkers.userinterface.chrome._

trait UserInterfaceModule {
  lazy val gameScreen: GameScreen = wire[GameScreen]

  lazy val gameOverPanel: GameOverPanel = wire[GameOverPanel]

  lazy val topChrome: TopChrome = wire[TopChrome]

  lazy val sideChrome: SideChrome = wire[SideChrome]

  lazy val sceneContainer: SceneContainer = wire[SceneContainer]

  lazy val jumpIndicator: JumpIndicator = wire[JumpIndicator]

  lazy val playerPanel: PlayerPanel = wire[PlayerPanel]

  lazy val turnIndicator: TurnIndicator = wire[TurnIndicator]

  lazy val button: Button = wire[Button]

  lazy val rushButton: RushButton = wire[RushButton]

  lazy val pieceAvatar: PieceAvatar = wire[PieceAvatar]
}
