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

  /**
    * An animation that temporarily hides one of the static pieces on the board (such as when moving a piece)
    */
  trait HidesStaticPiece {
    def hidesPieceAtSquare: Int
  }

  trait OneTimeAnimation extends Animation {
    def linearProgress(nowTime: Double): Double =
      math.max((nowTime - startTime) / duration, 1.0)

    def isExpired(nowTime: Double): Boolean =
      (nowTime - startTime) >= duration
  }

  case class MovingPiece(fromSquare: Int,
                         toSquare: Int,
                         startTime: Double,
                         duration: Double) extends OneTimeAnimation with HidesStaticPiece {
    def hidesPieceAtSquare = toSquare
  }

  case class JumpingPiece(fromSquare: Int,
                          toSquare: Int,
                          startTime: Double,
                          duration: Double) extends OneTimeAnimation with HidesStaticPiece {
    def hidesPieceAtSquare = toSquare
  }

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