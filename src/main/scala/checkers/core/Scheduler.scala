package checkers.core

trait ComputationProcess {
  def runComputations(maxCycles: Int): Int
}

trait Scheduler {
  def executeSlice(process: ComputationProcess): Unit
}