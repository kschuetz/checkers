package checkers.core

class DefaultScheduler extends Scheduler {
  def executeSlice(process: ComputationProcess): Unit = {
    process.runComputations(2500)
  }
}