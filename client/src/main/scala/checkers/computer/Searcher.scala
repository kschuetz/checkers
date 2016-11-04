package checkers.computer

import checkers.consts._
import checkers.core.Play.{Move, NoPlay}
import checkers.core._
import checkers.core.tables.JumpTable
import checkers.logger
import checkers.test.BoardUtils
import org.scalajs.dom

import scala.annotation.elidable
import scala.annotation.elidable._

object Searcher {
  val MaxDepth = 40

  val Infinity = Int.MaxValue - 1000
}

class Searcher(moveGenerator: MoveGenerator,
               moveExecutor: MoveExecutor,
               evaluator: Evaluator) {

  val random = new java.util.Random

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

    var tempErrorCount = 0

    val pv = new PrincipalVariation[Play](Searcher.MaxDepth)
    private val moveDecoder = new MoveDecoder
    private var iteration = 0
    private var done = false
    private var stack: Ply = _
    private var alphaCutoffCount = 0
    private var betaCutoffCount = 0
    private var deadEndCount: Int = 0
    private val boardStack = BoardStack.fromBoard(playInput.board)
    private var boardStackMaxLevel: Int = 0

//    log.debug(boardStack.toImmutable.toString)
//    boardStack.push()
//    boardStack.setOccupant(28, DARKKING)
//    log.debug(boardStack.toImmutable.toString)
//    boardStack.pop()
//    log.debug(boardStack.toImmutable.toString)
//
//    boardStack.push() // temp

    var probeA: Int = 0
    var probeB: Int = 0

    private val scoreBeforeMove = evaluator.evaluate(playInput.turnToMove, boardStack)
    private val rootCandidates: MoveList = moveGenerator.generateMoves(boardStack, playInput.turnToMove)

    trait Ply {
      def process: Ply
    }

    trait PlyParent {
      def answer(value: Int): Ply
    }

    object NullPlyParent extends PlyParent {
      def answer(value: Int): Ply = {
        log.debug(s"root ${-value}")
        null
      }
    }

    class ConcretePly(root: Boolean,
                      left: Boolean,
                      turnToMove: Color,
                      depthRemaining: Int,
                      plyIndex: Int,
                      parent: PlyParent,
                      rootMoveIndex: Int,
                      var alpha: Int,
                      val beta: Int) extends Ply with PlyParent {
      private var initted = false

      private var lastMove: Play = NoPlay

      private var candidates: MoveList = _

      private var captureBoard: BoardStateRead = _

      private var moveCount = 0
      private var nextMovePtr = 0

      private def init(): Unit = {
        val pvMove: Play = if (left) pv.getBestMove(plyIndex) else null
        candidates = {
          captureBoard = boardStack.toImmutable

          val base = if (root) {
            rootCandidates
          } else {
            val b1 = boardStack.toImmutable
            val temp = moveGenerator.generateMoves(boardStack, turnToMove)
            assert(BoardUtils.boardStatesEqual(b1, boardStack.toImmutable), "move generator corrupted board stack")
            temp
          }

          pvMove match {
            case m: Move =>
              probeA += 1
              base.moveToFrontIfExists(m.path, moveDecoder)
            case _ => base
          }
        }

        moveCount = candidates.count
        initted = true
      }

//      def process: Ply = {
//        val saveLevel = boardStack.level
//        val result = process2
//        val newLevel = boardStack.level
//
//        assert(saveLevel == newLevel, "stack level off")
//        result
//      }

      def process: Ply = {
        var retval: Ply = null

//        if(captureBoard != null) {
//          val currentBoard = boardStack.toImmutable
//          var corrupted = false
//          if (!BoardUtils.boardStatesEqual(currentBoard, captureBoard)) {
//            corrupted = true
//            if (tempErrorCount < 10) {
//              tempErrorCount += 1
//
//              println("---")
//              println(s"Corrupted: $plyIndex:$nextMovePtr")
//              println("current board:")
//              println(currentBoard.toString)
//              println("board at start:")
//              println(captureBoard.toString)
//              println(s"stack ${boardStack.level}")
//              println(BoardStack.juxtaposedDebugString(boardStack))
//            }
//          }
//          if (corrupted) return parent.answer(alpha)
//          assert(BoardUtils.boardStatesEqual(currentBoard, captureBoard), s"board stack corrupted! $nextMovePtr")
//        }

        if (plyIndex > deepestPly) deepestPly = plyIndex
        val stackLevel = boardStack.level
        if(stackLevel > boardStackMaxLevel) boardStackMaxLevel = stackLevel

        val leaf = if (depthRemaining <= 0) {
          !moveGenerator.mustJump(boardStack, turnToMove)
        } else false

        if (leaf) {
          evaluatorCalls += 1
//          val score = random.nextInt()
          val score = evaluator.evaluate(turnToMove, boardStack)

//          if(iteration < 4) {
//            log.debug(s"value: $rootMoveIndex = $score")
//          }

          parent.answer(score)
        } else {
          if (!initted) init()

          if (moveCount <= 0) {
            // loss
            deadEndCount += 1
            val score = -Searcher.Infinity + depthRemaining
            parent.answer(score)
          } else {

            if (nextMovePtr < moveCount) {
              if(root) log.debug(s"root - move $nextMovePtr")

              if(nextMovePtr == moveCount - 1) probeB += 1

                          val saveDP = boardStack.darkPieces
                          val saveLP = boardStack.lightPieces
                          val saveK = boardStack.kings

              if(captureBoard != null) {
                val currentBoard = boardStack.toImmutable
                var corrupted = false
                if (!BoardUtils.boardStatesEqual(currentBoard, captureBoard)) {
                  corrupted = true
                  if (tempErrorCount < 10) {
                    tempErrorCount += 1

                    println("---")
                    println(s"Corrupted: $plyIndex:$nextMovePtr")
                    println("current board:")
                    println(currentBoard.toString)
                    println("board at start:")
                    println(captureBoard.toString)
                    println(s"stack ${boardStack.level}")
                    println(BoardStack.juxtaposedDebugString(boardStack))
                  }
                }
                if (corrupted) return parent.answer(alpha)
                assert(BoardUtils.boardStatesEqual(currentBoard, captureBoard), s"board stack corrupted! $nextMovePtr")
              }

              val saveLevel = boardStack.level
              boardStack.push()
              assert(boardStack.level == saveLevel + 1)
              try {
                moveDecoder.load(candidates, nextMovePtr)
                nextMovePtr += 1

                {
                  val path = moveDecoder.pathToList
                  val piece = boardStack.getOccupant(path.head)

                  if(piece == EMPTY && tempErrorCount < 10) {
                    println(s"ERROR -- $plyIndex")
                    println(s"moveIndex ${nextMovePtr - 1}: ${path.toString}")
                    println(boardStack.toString)
                    println(candidates)
                    val candidates2 = moveGenerator.generateMoves(boardStack, turnToMove)
                    println("candidates2:")
                    println(candidates2)
                    println("board at start")
                    println(captureBoard.toString)
                    println("----")

                    tempErrorCount += 1
                  }

                  assert(piece != EMPTY, s"square is empty! (${path.toString})  $plyIndex")
                  assert(COLOR(piece) == turnToMove, "wrong color!")
                  lastMove = Move(path, proposeDraw = false)
                }

                moveExecutor.executeFromMoveDecoder(boardStack, moveDecoder)

                assert(
                  (boardStack.darkPieces != saveDP) ||
                  (boardStack.lightPieces != saveLP),  "board stack didn't change")

                //              val wasJump = moveExecutor.executeFromMoveDecoder(boardStack, moveDecoder)
                //              val nextDepthRemaining = {
                //                if(wasJump && depthRemaining == 1) 1   // quiescence
                //                else depthRemaining - 1
                //              }
                val nextDepthRemaining = math.max(0, depthRemaining - 1)

                val nextPly = new ConcretePly(
                  root = false,
                  left = left && nextMovePtr == 1,
                  turnToMove = OPPONENT(turnToMove),
                  depthRemaining = nextDepthRemaining,
                  plyIndex = plyIndex + 1,
                  parent = this,
                  rootMoveIndex = if(root) nextMovePtr - 1 else rootMoveIndex,
                  alpha = -beta,
                  beta = -alpha
                )

                retval = nextPly.process
              } finally {
                boardStack.pop()

                              assert(boardStack.darkPieces == saveDP)
                              assert(boardStack.lightPieces == saveLP)
                              assert(boardStack.kings == saveK)
              }
              retval
            } else {
              parent.answer(alpha)
            }
          }
        }
      }

      def answer(result: Int): Ply = {
        val value = -result
        if (!root && value >= beta) {
          betaCutoffCount += 1
          parent.answer(beta)
        } else {
          if(plyIndex == 0) log.debug(s"answer: $lastMove, $value, alpha=$alpha")
          if (value > alpha) {
            alphaCutoffCount += 1
            alpha = value
            //val lastMove = Move(moveDecoder.pathToList, proposeDraw = false)
//            if(root) probeB += 1

            pv.updateBestMove(plyIndex, lastMove)
          }
          this
        }
      }
    }

    private def createRootPly: Ply = new ConcretePly(
      root = true,
      left = true,
      turnToMove = playInput.turnToMove,
      depthRemaining = iteration,
      plyIndex = 0,
      parent = NullPlyParent,
      rootMoveIndex = -1,
      alpha = -Searcher.Infinity,
      beta = Searcher.Infinity)


    private def process(): Unit = {
      if (stack == null) {
        if (iteration < maxDepth) {
          iteration += 1
          log.debug(s"iteration: $iteration")

          dumpPv()

          stack = createRootPly
        } else {
          done = true
          return
        }
      }
      stack = stack.process
    }

    override def run(maxCycles: Int): Int = {
      val limit = if (cyclesLimited) {
        math.min(maxCycles, cyclesRemaining)
      } else maxCycles

      var steps = 0
      while (steps < limit && !done) {
        process()
        steps += 1
      }

      if (cyclesLimited) {
        cyclesRemaining -= steps
        if (cyclesRemaining <= 0) done = true
      }

      totalCyclesUsed += steps
      steps
    }

    override def interrupt(): Unit = {
      done = true
    }

    override def isReady: Boolean = done

    override def result: PlayResult = if (done) {
      val play = pv.getBestMove(0)
      logStats(play)
      transformResult(PlayResult(play, incomingPlayerState))
    } else throw new Exception("No result yet")

    @elidable(INFO)
    private def logStats(play: Play): Unit = {
      log.info("----------")
      log.info(s"Total cycles used: $totalCyclesUsed")
      log.info(s"Score before move: $scoreBeforeMove")
      log.info(s"Deepest ply: $deepestPly")
      log.info(s"Evaluations: $evaluatorCalls")
      log.info(s"Alpha cutoffs: $alphaCutoffCount")
      log.info(s"Beta cutoffs: $betaCutoffCount")
      log.info(s"Dead ends: $deadEndCount")
      log.info(s"Probe A: $probeA")
      log.info(s"Probe B: $probeB")
      log.info(s"Board stack level:  ${boardStack.level}")
      log.info(s"Board stack max level:  $boardStackMaxLevel")

      play match {
        case m: Move =>
          val candidateCount = rootCandidates.count
          val moveIndex = rootCandidates.indexOf(m.path)
          log.info(s"Chose move $moveIndex ($candidateCount candidates)")
          log.info(rootCandidates.toList.toString)
        case _ => ()
      }


    }

    @elidable(FINE)
    private def dumpPv(): Unit = {
      val sb = new StringBuilder
      var i = 0
      while (i < pv.depth) {
        val move = pv.getBestMove(i)
        if (move != null) {
          move match {
            case m: Move =>
              val s = m.path.mkString("->")
              sb.append(s)
              sb.append(" | ")
            case _ => ()
          }
          i += 1
        } else i = pv.depth

      }
      //dom.console.log(pv.line)
      log.debug(sb.result())
    }
  }

}
