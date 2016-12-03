package checkers.modules

import checkers.userinterface.board.{PhysicalBoard, SquareOverlayButton}
import com.softwaremill.macwire.wire
import checkers.userinterface.{DynamicScene, GameScreen, SceneContainer, SceneFrame}
import checkers.userinterface.chrome._
import checkers.userinterface.dialog.NewGameDialog
import checkers.userinterface.piece._

trait UserInterfaceModule {
  lazy val gameScreen: GameScreen = wire[GameScreen]

  lazy val gameOverPanel: GameOverPanel = wire[GameOverPanel]

  lazy val topChrome: TopChrome = wire[TopChrome]

  lazy val sideChrome: SideChrome = wire[SideChrome]

  lazy val sceneContainer: SceneContainer = wire[SceneContainer]

  lazy val sceneFrame: SceneFrame = wire[SceneFrame]

  lazy val dynamicScene: DynamicScene = wire[DynamicScene]

  lazy val physicalBoard: PhysicalBoard = wire[PhysicalBoard]

  lazy val jumpIndicator: JumpIndicator = wire[JumpIndicator]

  lazy val playerPanel: PlayerPanel = wire[PlayerPanel]

  lazy val turnIndicator: TurnIndicator = wire[TurnIndicator]

  lazy val thinkingIndicator: ThinkingIndicator = wire[ThinkingIndicator]

  lazy val button: Button = wire[Button]

  lazy val rushButton: RushButton = wire[RushButton]

  lazy val pieceAvatar: PieceAvatar = wire[PieceAvatar]

  lazy val squareOverlayButton: SquareOverlayButton = wire[SquareOverlayButton]
  
  lazy val crowningAnimation: CrowningAnimation = wire[CrowningAnimation]

  lazy val jumpingAnimation: JumpingPieceAnimation = wire[JumpingPieceAnimation]

  lazy val movingAnimation: MovingPieceAnimation = wire[MovingPieceAnimation]

  lazy val removingAnimation: RemovingPieceAnimation = wire[RemovingPieceAnimation]

  lazy val placingAnimation: PlacingPieceAnimation = wire[PlacingPieceAnimation]

  lazy val illegalPieceSelectionAnimation: IllegalPieceSelectionAnimation = wire[IllegalPieceSelectionAnimation]

  lazy val pickedUpPiece: PickedUpPiece = wire[PickedUpPiece]

  lazy val physicalPiece: PhysicalPiece = wire[PhysicalPiece]

  lazy val decorations: Decorations = wire[Decorations]

  lazy val newGameDialog: NewGameDialog = wire[NewGameDialog]

  lazy val animationEntryPoints: AnimationEntryPoints = DefaultAnimationEntryPoints
}
