package checkers.benchmarks

import checkers.benchmarks.suites.Sandbox
import org.scalajs.dom.document
import japgolly.scalajs.benchmark.gui.BenchmarkGUI

import scala.scalajs.js.annotation.JSExport

@JSExport("BenchmarksMain")
object Main extends scalajs.js.JSApp {

  def main(): Unit = {

    // import concurrent.duration._
    // import japgolly.scalajs.benchmark.engine.Options
    // val opts = Options.Default.copy(minRuns = 1000, minTime = 0.millis)

    val tgt = document.getElementById("body")

    BenchmarkGUI.renderMenu(tgt)(Sandbox.suite)
  }
}
