package checkers.game

import checkers.geometry.Point

sealed trait Animation {
  def startTime: Double

  def duration: Double

  def linearProgress(nowTime: Double): Double

  def hasStarted(nowTime: Double): Boolean = nowTime >= startTime

  def isExpired(nowTime: Double): Boolean
}

object Animation {

  trait OneTimeAnimation extends Animation {
    def linearProgress(nowTime: Double): Double =
      math.max((nowTime - startTime) / duration, 1.0)

    def isExpired(nowTime: Double): Boolean =
      (nowTime - startTime) >= duration
  }

  case class MovingPiece(fromSquare: Int,
                         toSquare: Int,
                         startTime: Double,
                         duration: Double) extends OneTimeAnimation

  case class JumpingPiece(fromSquare: Int,
                          toSquare: Int,
                          startTime: Double,
                          duration: Double) extends OneTimeAnimation

  case class RemovingPiece(piece: Piece,
                           fromSquare: Int,
                           exitPoint: Point,
                           startTime: Double,
                           duration: Double) extends OneTimeAnimation

  case class PromotingPiece(square: Int,
                            entrancePoint: Point,
                            startTime: Double,
                            duration: Double) extends OneTimeAnimation

  case class HintAnimation(fromSquare: Int,
                           toSquare: Int,
                           startTime: Double,
                           duration: Double) extends OneTimeAnimation

  case class FlippingBoardAnimation(targetOrientation: BoardOrientation,
                                    startTime: Double,
                                    duration: Double) extends OneTimeAnimation


}