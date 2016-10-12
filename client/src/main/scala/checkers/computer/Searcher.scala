package checkers.computer

import checkers.consts._
import checkers.core.Play.Move
import checkers.core._

class Searcher(moveGenerator: MoveGenerator,
               moveExecutor: MoveExecutor,
               evaluator: Evaluator) {

  class Search(playInput: PlayInput, incomingPlayerState: ComputerPlayerState, maxDepth: Int) extends PlayComputation {

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
        boardStack.push()
        try {
          if (nextMovePtr < moveCount) {
            moveDecoder.load(candidates, nextMovePtr)
            moveExecutor.executeFromMoveDecoder(boardStack, moveDecoder)
            if (depth <= 0) {
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
      var steps = 0
      while (steps < maxCycles && !done) {
        process()
        steps += 1
      }
      steps
    }

    override def interrupt(): Unit = {
      done = true
    }

    override def isReady: Boolean = done

    override def result: (Play, Opaque) = if (done) {
      (pv.getBestMove(0), incomingPlayerState)
    } else throw new Exception("No result yet")
  }

}
