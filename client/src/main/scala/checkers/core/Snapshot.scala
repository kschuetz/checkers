package checkers.core

import checkers.consts.Side

case class Snapshot(gameClock: Double,
                    turnIndex: Int,
                    turnToMove: Side,
                    board: BoardState,
                    drawStatus: DrawStatus,
                    darkState: PlayerState,
                    lightState: PlayerState,
                    darkScore: Int,
                    lightScore: Int)

