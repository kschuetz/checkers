package checkers.computer

import checkers.consts._
import checkers.core.Play.Move
import checkers.core._
import checkers.logger

object Searcher {
  val MaxDepth = 32
}

class Searcher(moveGenerator: MoveGenerator,
               moveExecutor: MoveExecutor,
               evaluator: Evaluator) {

  val log = logger.computerPlayer

  def create(playInput: PlayInput, incomingPlayerState: ComputerPlayerState, depthLimit: Option[Int],
             cycleLimit: Option[Int], transformResult: PlayResult => PlayResult): Search =
    new Search(playInput, incomingPlayerState, depthLimit, cycleLimit, transformResult)

  class Search(playInput: PlayInput,
               incomingPlayerState: ComputerPlayerState,
               depthLimit: Option[Int],
               cycleLimit: Option[Int],
               transformResult: PlayResult => PlayResult)
    extends PlayComputation {

    val maxDepth = depthLimit.getOrElse(Searcher.MaxDepth)
    var cyclesRemaining = cycleLimit.getOrElse(0)
    val cyclesLimited = cycleLimit.nonEmpty
    var totalCyclesUsed = 0
    var deepestPly = 0
    var evaluatorCalls = 0

    val pv = new PrincipalVariation[Play](maxDepth)
    private var iteration = 0
    private var done = false
    private var stack: Ply = _
    private var alphaCutoffCount = 0
    private var betaCutoffCount = 0

    private val boardStack = BoardStack.fromBoard(playInput.board)

    trait PlyParent {
      def answer(value: Int): Ply
    }

    object NullPlyParent extends PlyParent {
      def answer(value: Int): Ply = null
    }

    class Ply(root: Boolean,
              left: Boolean,
              turnToMove: Color,
              depth: Int,
              plyIndex: Int,
              parent: PlyParent,
              var alpha: Int,
              var beta: Int) extends PlyParent {

      val candidates = moveGenerator.generateMoves(boardStack, turnToMove)
      val moveCount = candidates.count
      var nextMovePtr = 0
      val gameOver = moveCount == 0
      val moveDecoder = new MoveDecoder

      // TODO: handle case of only one move
      // TODO: handle case of game over

      def process: Ply = {
        if(plyIndex > deepestPly) deepestPly = plyIndex
        boardStack.push()
        try {
          if (nextMovePtr < moveCount) {
            moveDecoder.load(candidates, nextMovePtr)
            nextMovePtr += 1
            moveExecutor.executeFromMoveDecoder(boardStack, moveDecoder)
            if (depth <= 0) {
              evaluatorCalls += 1
              val value = evaluator.evaluate(turnToMove, boardStack)
              if (value >= beta) {
                betaCutoffCount += 1
                parent.answer(beta)
              } else {

                if (value > alpha) {
                  alpha = value
                  alphaCutoffCount += 1
                  val move = Move(moveDecoder.pathToList, proposeDraw = false)
                  pv.updateBestMove(plyIndex, move)
                }

                this
              }

            } else {
              val nextPly = new Ply(
                root = false,
                left = left && nextMovePtr == 1,
                turnToMove = OPPONENT(turnToMove),
                depth = depth - 1,
                plyIndex = plyIndex + 1,
                parent = this,
                alpha = -beta,
                beta = -alpha
              )

              nextPly.process
            }

          } else {
            parent.answer(alpha)
          }
        } finally {
          boardStack.pop()
        }
      }

      def answer(result: Int): Ply = {
        val value = -result
        if (value > beta) parent.answer(beta)
        else {
          if (value > alpha) {
            alpha = value
            val lastMove = Move(moveDecoder.pathToList, proposeDraw = false)
            pv.updateBestMove(plyIndex, lastMove)
          }
          this
        }
      }
    }


    private def createRootPly: Ply = new Ply(
      root = true,
      left = true,
      turnToMove = playInput.turnToMove,
      depth = iteration - 1,
      plyIndex = 0,
      parent = NullPlyParent,
      alpha = Int.MinValue,
      beta = Int.MaxValue)


    private def process(): Unit = {
      if (stack == null) {
        if (iteration < maxDepth) {
          iteration += 1
          stack = createRootPly
        } else {
          done = true
          return
        }
      }
      stack = stack.process
    }

    override def run(maxCycles: Int): Int = {
      val limit = if(cyclesLimited) {
        math.min(maxCycles, cyclesRemaining)
      } else maxCycles

      var steps = 0
      while (steps < limit && !done) {
        process()
        steps += 1
      }

      if(cyclesLimited) {
        cyclesRemaining -= steps
        if(cyclesRemaining <= 0) done = true
      }

      totalCyclesUsed += steps
      steps
    }

    override def interrupt(): Unit = {
      done = true
    }

    override def isReady: Boolean = done

    override def result: PlayResult = if (done) {
      logStats()
      val play = pv.getBestMove(0)
      transformResult(PlayResult(play, incomingPlayerState))
    } else throw new Exception("No result yet")

    private def logStats(): Unit = {
      log.info(s"Total cycles used: $totalCyclesUsed")
      log.info(s"Deepest ply: $deepestPly")
      log.info(s"Evaluator calls: $evaluatorCalls")
      log.info(s"Alpha cutoffs: $alphaCutoffCount")
      log.info(s"Beta cutoffs: $betaCutoffCount")
    }
  }

}
