package checkers.benchmarks.suites

import japgolly.scalajs.benchmark._, gui._

object Sandbox {
  val suite = GuiSuite(
    Suite("Example Benchmarks")(

      // Benchmark #1
      Benchmark("foreach") {
        var s = Set.empty[Int]
        (1 to 100) foreach (s += _)
        s
      },

      // Benchmark #2
      Benchmark("fold") {
        (1 to 100).foldLeft(Set.empty[Int])(_ + _)
      }
    )
  )
}