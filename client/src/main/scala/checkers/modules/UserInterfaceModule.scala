package checkers.modules

import checkers.core.Notation
import checkers.userinterface.animation._
import checkers.userinterface.board.{LastMoveIndicator, PhysicalBoard, SquareOverlayButton}
import checkers.userinterface.chrome._
import checkers.userinterface.dialog.NewGameDialog
import checkers.userinterface.gamelog.{GameLogDisplay, GameLogEntry}
import checkers.userinterface.piece._
import checkers.userinterface.widgets.{Arrow, Button, DirectedArrow, ScrollButton}
import checkers.userinterface.{DynamicScene, GameScreen, SceneContainer, SceneFrame}
import com.softwaremill.macwire.wire

trait UserInterfaceModule {
  protected def notation: Notation

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

  lazy val lastMoveIndicator: LastMoveIndicator = wire[LastMoveIndicator]

  lazy val powerMeter: PowerMeter = wire[PowerMeter]

  lazy val button: Button = wire[Button]

  lazy val rushButton: RushButton = wire[RushButton]

  lazy val scrollButton: ScrollButton = wire[ScrollButton]

  lazy val pieceAvatar: PieceAvatar = wire[PieceAvatar]

  lazy val squareOverlayButton: SquareOverlayButton = wire[SquareOverlayButton]
  
  lazy val crowningAnimation: CrowningAnimation = wire[CrowningAnimation]

  lazy val jumpingAnimation: JumpingPieceAnimation = wire[JumpingPieceAnimation]

  lazy val movingAnimation: MovingPieceAnimation = wire[MovingPieceAnimation]

  lazy val removingAnimation: RemovingPieceAnimation = wire[RemovingPieceAnimation]

  lazy val placingAnimation: PlacingPieceAnimation = wire[PlacingPieceAnimation]

  lazy val illegalPieceSelectionAnimation: IllegalPieceSelectionAnimation = wire[IllegalPieceSelectionAnimation]

  lazy val arrow: Arrow = wire[Arrow]

  lazy val directedArrow: DirectedArrow = wire[DirectedArrow]

  lazy val showHintAnimation: ShowHintAnimation = wire[ShowHintAnimation]

  lazy val pickedUpPiece: PickedUpPiece = wire[PickedUpPiece]

  lazy val physicalPiece: PhysicalPiece = wire[PhysicalPiece]

  lazy val decorations: Decorations = wire[Decorations]

  lazy val newGameDialog: NewGameDialog = wire[NewGameDialog]

  lazy val gameLogEntry: GameLogEntry = wire[GameLogEntry]

  lazy val gameLog: GameLogDisplay = wire[GameLogDisplay]

  lazy val animationEntryPoints: AnimationEntryPoints = DefaultAnimationEntryPoints
}
